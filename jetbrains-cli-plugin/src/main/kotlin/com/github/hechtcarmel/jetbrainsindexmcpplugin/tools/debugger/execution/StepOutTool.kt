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

class StepOutTool : AbstractMcpTool() {

    override val name = ToolNames.STEP_OUT

    override val description = """
        执行单步跳出（Step Out）。
        执行完当前方法的剩余代码并返回到调用者。
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

        return edtAction {
            try {
                session.stepOut()
                createJsonResult(ExecutionResult(
                    success = true,
                    sessionId = getSessionId(session),
                    state = getSessionState(session),
                    message = "已执行单步跳出"
                ))
            } catch (e: Exception) {
                createErrorResult("单步跳出失败: ${e.message}")
            }
        }
    }
}