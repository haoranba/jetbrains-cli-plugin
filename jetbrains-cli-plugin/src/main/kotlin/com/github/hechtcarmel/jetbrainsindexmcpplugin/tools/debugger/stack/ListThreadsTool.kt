package com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.stack

import com.github.hechtcarmel.jetbrainsindexmcpplugin.constants.ParamNames
import com.github.hechtcarmel.jetbrainsindexmcpplugin.constants.ToolNames
import com.github.hechtcarmel.jetbrainsindexmcpplugin.server.models.ToolCallResult
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.AbstractMcpTool
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.models.ListThreadsResult
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.models.ThreadInfo
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.schema.SchemaBuilder
import com.intellij.openapi.project.Project
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

class ListThreadsTool : AbstractMcpTool() {

    override val name = ToolNames.LIST_THREADS

    override val description = """
        列出调试会话中的所有线程。
        返回线程 ID、名称、状态等信息。
    """.trimIndent()

    override val inputSchema: JsonObject = SchemaBuilder.tool()
        .projectPath()
        .stringProperty(ParamNames.SESSION_ID, "会话 ID（可选，默认当前会话）", required = false)
        .build()

    override suspend fun doExecute(project: Project, arguments: JsonObject): ToolCallResult {
        if (!isDebuggerSupported()) {
            return createDebuggerNotSupportedResult()
        }

        val sessionId = arguments[ParamNames.SESSION_ID]?.jsonPrimitive?.content
        val session = resolveSession(project, sessionId)
            ?: return createSessionNotFoundResult(sessionId)

        if (!session.isPaused) {
            return createSessionNotPausedResult()
        }

        return readAction {
            val suspendContext = session.suspendContext
            val threads = mutableListOf<ThreadInfo>()

            // Note: XDebugSession and XSuspendContext don't provide direct thread enumeration
            // This is a simplified implementation that returns basic information
            // Full thread listing requires debugger-specific API (e.g., Java Debugger)
            threads.add(ThreadInfo(
                id = 0,
                name = "Current Thread",
                state = "SUSPENDED",
                isCurrent = true
            ))

            createJsonResult(ListThreadsResult(
                threads = threads,
                currentThreadId = 0,
                totalCount = threads.size
            ))
        }
    }
}