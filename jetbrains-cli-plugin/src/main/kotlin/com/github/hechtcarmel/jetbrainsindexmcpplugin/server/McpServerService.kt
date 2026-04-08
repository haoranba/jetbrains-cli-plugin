package com.github.hechtcarmel.jetbrainsindexmcpplugin.server

import com.github.hechtcarmel.jetbrainsindexmcpplugin.McpBundle
import com.github.hechtcarmel.jetbrainsindexmcpplugin.McpConstants
import com.github.hechtcarmel.jetbrainsindexmcpplugin.ServerStatusListener
import com.github.hechtcarmel.jetbrainsindexmcpplugin.server.transport.KtorServer
import com.github.hechtcarmel.jetbrainsindexmcpplugin.settings.McpSettings
import com.github.hechtcarmel.jetbrainsindexmcpplugin.settings.McpSettingsConfigurable
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.ToolRegistry
import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.options.ShowSettingsUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Application-level service managing the server infrastructure for CLI access.
 *
 * This service manages:
 * - Embedded Ktor CIO server with configurable port
 * - Tool registry for all available tools
 * - JSON-RPC handler for message processing
 * - Coroutine scope for non-blocking tool execution
 */
@Service(Service.Level.APP)
class McpServerService(
    private val coroutineScope: CoroutineScope
) : Disposable {

    private val toolRegistry: ToolRegistry = ToolRegistry()
    private val jsonRpcHandler: JsonRpcHandler
    private var ktorServer: KtorServer? = null
    private var serverError: ServerError? = null

    /**
     * Represents a server error state.
     */
    data class ServerError(
        val message: String,
        val port: Int? = null
    )

    @Volatile
    var isInitialized: Boolean = false
        private set

    companion object {
        private val LOG = logger<McpServerService>()

        fun getInstance(): McpServerService = service()
    }

    init {
        LOG.info("Initializing Server Service")
        jsonRpcHandler = JsonRpcHandler(toolRegistry)
        // Self-initialize asynchronously so the server starts even if postStartupActivity
        // doesn't fire (see issue #73). initialize() is idempotent (@Synchronized + isInitialized
        // guard), so the redundant call from McpServerStartupActivity is a safe no-op.
        coroutineScope.launch { initialize() }
    }

    @Synchronized
    fun initialize() {
        if (isInitialized) return

        LOG.info("Performing deferred Server initialization")

        toolRegistry.registerBuiltInTools()

        val settings = McpSettings.getInstance()
        val port = settings.serverPort
        val host = settings.serverHost
        isInitialized = true
        startServer(host, port)

        LOG.info("Server Service initialized with Ktor CIO server")
    }

    /**
     * Starts the server on the specified port.
     *
     * @param host The host to bind to
     * @param port The port to listen on
     * @return The result of the start operation
     */
    fun startServer(host: String, port: Int): KtorServer.StartResult {
        // Stop existing server if running
        stopServer()

        LOG.info("Starting Server on $host:$port")

        val server = KtorServer(
            port = port,
            host = host,
            jsonRpcHandler = jsonRpcHandler
        )

        val result = when (val startResult = server.start()) {
            is KtorServer.StartResult.Success -> {
                ktorServer = server
                serverError = null
                LOG.info("Server started successfully on $host:$port")
                startResult
            }
            is KtorServer.StartResult.PortInUse -> {
                serverError = ServerError("Port $port is already in use", port)
                showErrorNotification(
                    McpBundle.message("notification.serverPortInUse.title"),
                    McpBundle.message("notification.serverPortInUse.content", port, host)
                )
                startResult
            }
            is KtorServer.StartResult.Error -> {
                serverError = ServerError(startResult.message)
                LOG.warn("Failed to start Server: ${startResult.message}", startResult.cause)
                showErrorNotification(
                    McpBundle.message("notification.serverStartFailed.title"),
                    McpBundle.message("notification.serverStartFailed.content", startResult.message)
                )
                startResult
            }
        }

        // Notify listeners that server status changed
        notifyStatusChanged()

        return result
    }

    /**
     * Notifies all listeners that the server status has changed.
     */
    private fun notifyStatusChanged() {
        ApplicationManager.getApplication().invokeLater({
            ApplicationManager.getApplication().messageBus
                .syncPublisher(McpConstants.SERVER_STATUS_TOPIC)
                .serverStatusChanged()
        }, ModalityState.any())
    }

    /**
     * Stops the server.
     */
    fun stopServer() {
        ktorServer?.stop()
        ktorServer = null
    }

    /**
     * Restarts the server on a new host/port.
     *
     * @param newHost The new host to bind to
     * @param newPort The new port to listen on
     * @return The result of the restart operation
     */
    fun restartServer(newHost: String, newPort: Int): KtorServer.StartResult {
        LOG.info("Restarting Server on $newHost:$newPort")
        return startServer(newHost, newPort)
    }

    /**
     * Returns whether the server is currently running.
     */
    fun isServerRunning(): Boolean = ktorServer?.isRunning() == true

    /**
     * Returns the current server error, if any.
     */
    fun getServerError(): ServerError? = serverError

    fun getToolRegistry(): ToolRegistry = toolRegistry

    fun getJsonRpcHandler(): JsonRpcHandler = jsonRpcHandler

    /**
     * Returns the server URL for CLI connections.
     *
     * @return The server URL, or null if server is not running
     */
    fun getServerUrl(): String? {
        if (ktorServer == null || serverError != null) return null
        val settings = McpSettings.getInstance()
        val port = settings.serverPort
        val host = settings.serverHost
        return "http://$host:$port/api"
    }

    /**
     * Returns the configured server port.
     */
    fun getServerPort(): Int = McpSettings.getInstance().serverPort

    /**
     * Returns information about the server status.
     */
    fun getServerInfo(): ServerStatusInfo {
        val settings = McpSettings.getInstance()
        val port = settings.serverPort
        val host = settings.serverHost
        val isRunning = isServerRunning()
        return ServerStatusInfo(
            name = McpConstants.SERVER_NAME,
            version = McpConstants.SERVER_VERSION,
            apiUrl = if (isRunning) "http://$host:$port/api" else "Server not running",
            port = port,
            registeredTools = toolRegistry.getAllTools().size,
            error = serverError?.message,
            isRunning = isRunning
        )
    }

    /**
     * Shows an error notification with an action to open settings.
     */
    private fun showErrorNotification(title: String, content: String) {
        ApplicationManager.getApplication().invokeLater({
            NotificationGroupManager.getInstance()
                .getNotificationGroup(McpConstants.NOTIFICATION_GROUP_ID)
                .createNotification(
                    title,
                    content,
                    NotificationType.ERROR
                )
                .addAction(object : NotificationAction(McpBundle.message("notification.action.openSettings")) {
                    override fun actionPerformed(e: AnActionEvent, notification: Notification) {
                        ShowSettingsUtil.getInstance().showSettingsDialog(null, McpSettingsConfigurable::class.java)
                        notification.expire()
                    }
                })
                .notify(null)
        }, ModalityState.any())
    }

    override fun dispose() {
        LOG.info("Disposing Server Service")
        stopServer()
    }
}

/**
 * Data class containing server status information.
 */
data class ServerStatusInfo(
    val name: String,
    val version: String,
    val apiUrl: String,
    val port: Int,
    val registeredTools: Int,
    val error: String? = null,
    val isRunning: Boolean = true
)