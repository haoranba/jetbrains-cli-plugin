package com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.session

import com.github.hechtcarmel.jetbrainsindexmcpplugin.constants.ParamNames
import com.github.hechtcarmel.jetbrainsindexmcpplugin.constants.ToolNames
import com.github.hechtcarmel.jetbrainsindexmcpplugin.server.models.ToolCallResult
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.AbstractMcpTool
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.models.StopDebugSessionResult
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.schema.SchemaBuilder
import com.intellij.openapi.project.Project
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

class StopDebugSessionTool : AbstractMcpTool() {

    override val name = ToolNames.STOP_DEBUG_SESSION

    override val description = """
        停止指定的调试会话。
        如果未指定会话 ID，则停止当前活动会话。
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

        return edtAction {
            try {
                session.stop()
                createJsonResult(StopDebugSessionResult(
                    success = true,
                    message = "调试会话已停止"
                ))
            } catch (e: Exception) {
                createErrorResult("停止会话失败: ${e.message}")
            }
        }
    }
}