package com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.evaluation

import com.github.hechtcarmel.jetbrainsindexmcpplugin.constants.ParamNames
import com.github.hechtcarmel.jetbrainsindexmcpplugin.constants.ToolNames
import com.github.hechtcarmel.jetbrainsindexmcpplugin.server.models.ToolCallResult
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.AbstractMcpTool
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.models.SetVariableResult
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.schema.SchemaBuilder
import com.intellij.openapi.project.Project
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

class SetVariableTool : AbstractMcpTool() {

    override val name = ToolNames.SET_VARIABLE

    override val description = """
        设置变量的值。
        需要会话处于暂停状态。
        注意：由于 IntelliJ API 限制，此功能当前不可用。
    """.trimIndent()

    override val inputSchema: JsonObject = SchemaBuilder.tool()
        .projectPath()
        .stringProperty(ParamNames.VARIABLE_NAME, "变量名", required = true)
        .stringProperty(ParamNames.VALUE, "新值表达式", required = true)
        .stringProperty(ParamNames.SESSION_ID, "会话 ID（可选，默认当前会话）", required = false)
        .build()

    override suspend fun doExecute(project: Project, arguments: JsonObject): ToolCallResult {
        if (!isDebuggerSupported()) {
            return createDebuggerNotSupportedResult()
        }

        val variableName = arguments[ParamNames.VARIABLE_NAME]?.jsonPrimitive?.content
            ?: return createErrorResult("缺少必需参数: ${ParamNames.VARIABLE_NAME}")

        val value = arguments[ParamNames.VALUE]?.jsonPrimitive?.content
            ?: return createErrorResult("缺少必需参数: ${ParamNames.VALUE}")

        val sessionId = arguments[ParamNames.SESSION_ID]?.jsonPrimitive?.content
        val session = resolveSession(project, sessionId)
            ?: return createSessionNotFoundResult(sessionId)

        if (!session.isPaused) {
            return createSessionNotPausedResult()
        }

        // Note: XDebugSession doesn't provide direct API to set variable values
        // This is a placeholder that acknowledges the limitation
        return edtAction {
            createJsonResult(SetVariableResult(
                success = false,
                name = variableName,
                newValue = value,
                message = "设置变量功能需要调试器特定实现，当前不支持"
            ))
        }
    }
}