package com.github.hechtcarmel.jetbrainsindexmcpplugin.server.transport

import com.github.hechtcarmel.jetbrainsindexmcpplugin.McpConstants
import com.github.hechtcarmel.jetbrainsindexmcpplugin.server.JsonRpcHandler
import com.github.hechtcarmel.jetbrainsindexmcpplugin.server.models.JsonRpcError
import com.github.hechtcarmel.jetbrainsindexmcpplugin.server.models.JsonRpcResponse
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.application.asContextElement
import com.intellij.openapi.diagnostic.logger
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.net.BindException

/**
 * Simplified Ktor CIO server for CLI access.
 *
 * Provides stateless JSON-RPC 2.0 endpoints:
 * - POST /api → JSON-RPC 2.0 request handling
 * - GET /api/tools → List all available tools
 * - GET /api/health → Health check
 */
class KtorServer(
    private val port: Int,
    private val host: String = McpConstants.DEFAULT_SERVER_HOST,
    private val jsonRpcHandler: JsonRpcHandler
) : Disposable {

    private var server: EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine.Configuration>? = null

    companion object {
        private val LOG = logger<KtorServer>()
        private const val API_PATH = "/api"
        private const val TOOLS_PATH = "/api/tools"
        private const val HEALTH_PATH = "/api/health"
    }

    /**
     * Result of attempting to start the server.
     */
    sealed class StartResult {
        data object Success : StartResult()
        data class PortInUse(val port: Int) : StartResult()
        data class Error(val message: String, val cause: Throwable? = null) : StartResult()
    }

    /**
     * Starts the server.
     */
    fun start(): StartResult {
        return try {
            server = embeddedServer(CIO, port = port, host = host) {
                configureRouting()
            }
            server?.start(wait = false)

            LOG.info("Server started on http://$host:$port")
            StartResult.Success
        } catch (e: BindException) {
            LOG.warn("Port $port is already in use", e)
            StartResult.PortInUse(port)
        } catch (e: Exception) {
            if (e is CancellationException) {
                val cause = e.cause
                if (cause is BindException) {
                    LOG.warn("Failed to start server on $host:$port: ${cause.message}", cause)
                    return StartResult.Error("Failed to bind to $host:$port. ${cause.message}", cause)
                }
                throw e
            }
            LOG.error("Failed to start server", e)
            StartResult.Error(e.message ?: "Unknown error", e)
        }
    }

    /**
     * Stops the server gracefully.
     */
    fun stop() {
        try {
            server?.stop(1000, 2000)
            server = null
            LOG.info("Server stopped")
        } catch (e: Exception) {
            LOG.warn("Error stopping server", e)
        }
    }

    /**
     * Returns whether the server is currently running.
     */
    fun isRunning(): Boolean = server != null

    override fun dispose() = stop()

    private fun Application.configureRouting() {
        routing {
            // POST /api - JSON-RPC 2.0 request handling
            post(API_PATH) {
                handleJsonRpcRequest(call)
            }

            // GET /api/tools - List all available tools
            get(TOOLS_PATH) {
                handleToolsList(call)
            }

            // GET /api/health - Health check
            get(HEALTH_PATH) {
                handleHealthCheck(call)
            }
        }
    }

    /**
     * Handles POST /api - JSON-RPC 2.0 request.
     */
    private suspend fun handleJsonRpcRequest(call: ApplicationCall) {
        val body = call.receiveText()

        if (body.isBlank()) {
            call.respondText(
                createJsonRpcError(null as JsonElement?, -32700, "Empty request body"),
                ContentType.Application.Json,
                HttpStatusCode.BadRequest
            )
            return
        }

        try {
            val response = runWithIdeModality {
                jsonRpcHandler.handleRequest(body)
            }
            if (response != null) {
                call.respondText(response, ContentType.Application.Json)
            } else {
                call.respond(HttpStatusCode.Accepted)
            }
        } catch (e: Exception) {
            LOG.error("Error processing JSON-RPC request", e)
            call.respondText(
                createJsonRpcError(null as JsonElement?, -32603, e.message ?: "Internal error"),
                ContentType.Application.Json
            )
        }
    }

    /**
     * Handles GET /api/tools - List all available tools.
     */
    private suspend fun handleToolsList(call: ApplicationCall) {
        try {
            val response = runWithIdeModality {
                jsonRpcHandler.handleRequest(
                    """{"jsonrpc":"2.0","id":1,"method":"tools/list"}"""
                )
            }
            if (response != null) {
                call.respondText(response, ContentType.Application.Json)
            } else {
                call.respond(HttpStatusCode.InternalServerError)
            }
        } catch (e: Exception) {
            LOG.error("Error listing tools", e)
            call.respondText(
                createJsonRpcError(null as JsonElement?, -32603, e.message ?: "Internal error"),
                ContentType.Application.Json
            )
        }
    }

    /**
     * Handles GET /api/health - Health check.
     */
    private suspend fun handleHealthCheck(call: ApplicationCall) {
        val healthInfo = mapOf(
            "status" to "ok",
            "version" to McpConstants.SERVER_VERSION,
            "port" to port
        )
        call.respondText(
            Json { encodeDefaults = true }.encodeToString(healthInfo),
            ContentType.Application.Json
        )
    }

    private val json = Json { encodeDefaults = true; prettyPrint = false }

    private suspend fun <T> runWithIdeModality(block: suspend () -> T): T {
        val application = ApplicationManager.getApplication()
        return if (application == null) {
            block()
        } else {
            withContext(ModalityState.any().asContextElement()) {
                block()
            }
        }
    }

    private fun createJsonRpcError(id: JsonElement?, code: Int, message: String): String {
        val response = JsonRpcResponse(
            id = id,
            error = JsonRpcError(code = code, message = message)
        )
        return json.encodeToString(response)
    }
}