package com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.execution

import com.github.hechtcarmel.jetbrainsindexmcpplugin.constants.ParamNames
import com.github.hechtcarmel.jetbrainsindexmcpplugin.constants.ToolNames
import com.github.hechtcarmel.jetbrainsindexmcpplugin.server.models.ToolCallResult
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.AbstractMcpTool
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.models.ExecutionResult
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.schema.SchemaBuilder
import com.intellij.openapi.project.Project
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

class PauseTool : AbstractMcpTool() {

    override val name = ToolNames.PAUSE_EXECUTION

    override val description = """
        暂停调试会话的执行。
        在当前位置暂停运行中的程序。
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

        // Note: Can pause even if not paused (i.e., while running)
        return edtAction {
            try {
                session.pause()
                createJsonResult(ExecutionResult(
                    success = true,
                    sessionId = getSessionId(session),
                    state = getSessionState(session),
                    message = "执行已暂停"
                ))
            } catch (e: Exception) {
                createErrorResult("暂停执行失败: ${e.message}")
            }
        }
    }
}