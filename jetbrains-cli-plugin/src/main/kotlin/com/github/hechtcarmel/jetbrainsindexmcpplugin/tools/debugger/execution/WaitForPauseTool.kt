package com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.execution

import com.github.hechtcarmel.jetbrainsindexmcpplugin.constants.ParamNames
import com.github.hechtcarmel.jetbrainsindexmcpplugin.constants.ToolNames
import com.github.hechtcarmel.jetbrainsindexmcpplugin.server.models.ToolCallResult
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.AbstractMcpTool
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.models.DebugSessionStatus
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.schema.SchemaBuilder
import com.intellij.openapi.project.Project
import com.intellij.xdebugger.XDebugSessionListener
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive

class WaitForPauseTool : AbstractMcpTool() {

    override val name = ToolNames.WAIT_FOR_PAUSE

    override val description = """
        等待调试会话暂停。
        阻塞等待直到程序暂停在断点或步进位置，或超时。
        默认超时时间为 30000 毫秒。
    """.trimIndent()

    override val inputSchema: JsonObject = SchemaBuilder.tool()
        .projectPath()
        .stringProperty(ParamNames.SESSION_ID, "会话 ID（可选，默认当前会话）", required = false)
        .intProperty(ParamNames.TIMEOUT_MS, "超时时间（毫秒），默认 30000", required = false)
        .build()

    override suspend fun doExecute(project: Project, arguments: JsonObject): ToolCallResult {
        if (!isDebuggerSupported()) {
            return createDebuggerNotSupportedResult()
        }

        val sessionId = arguments[ParamNames.SESSION_ID]?.jsonPrimitive?.content
        val session = resolveSession(project, sessionId)
            ?: return createSessionNotFoundResult(sessionId)

        val timeoutMs = arguments[ParamNames.TIMEOUT_MS]?.jsonPrimitive?.int ?: 30000

        // If already paused, return immediately
        if (session.isPaused) {
            return readAction {
                createJsonResult(getSessionStatus(session))
            }
        }

        // If session is stopped, return error
        if (session.isStopped) {
            return createErrorResult("调试会话已停止")
        }

        // Wait for pause with timeout using kotlinx.coroutines.selects pattern
        return try {
            withTimeout(timeoutMs.toLong()) {
                waitForPauseInternal(session)
            }
        } catch (e: TimeoutCancellationException) {
            val status = readAction { getSessionStatus(session) }
            createErrorResult("等待暂停超时，当前状态: ${status.state}")
        }
    }

    private suspend fun waitForPauseInternal(session: com.intellij.xdebugger.XDebugSession): ToolCallResult {
        return kotlinx.coroutines.suspendCancellableCoroutine { continuation ->
            val listener = object : XDebugSessionListener {
                private var isHandled = false

                override fun sessionPaused() {
                    if (isHandled) return
                    isHandled = true
                    session.removeSessionListener(this)

                    if (continuation.isActive) {
                        continuation.resume(
                            readAction { createJsonResult(getSessionStatus(session)) },
                            null
                        )
                    }
                }

                override fun sessionStopped() {
                    if (isHandled) return
                    isHandled = true
                    session.removeSessionListener(this)

                    if (continuation.isActive) {
                        continuation.resume(
                            createErrorResult("调试会话已停止"),
                            null
                        )
                    }
                }
            }

            session.addSessionListener(listener)

            continuation.invokeOnCancellation {
                session.removeSessionListener(listener)
            }
        }
    }

    private fun getSessionStatus(session: com.intellij.xdebugger.XDebugSession): DebugSessionStatus {
        val currentFrame = session.currentStackFrame
        val currentFile = currentFrame?.sourcePosition?.file?.path
        val currentLine = currentFrame?.sourcePosition?.line

        return DebugSessionStatus(
            sessionId = getSessionId(session),
            state = getSessionState(session),
            isPaused = session.isPaused,
            currentFile = currentFile,
            currentLine = currentLine?.plus(1),
            currentThreadName = null
        )
    }
}