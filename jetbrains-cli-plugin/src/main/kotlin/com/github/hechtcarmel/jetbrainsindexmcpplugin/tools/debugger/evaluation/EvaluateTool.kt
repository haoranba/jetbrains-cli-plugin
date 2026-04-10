package com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.evaluation

import com.github.hechtcarmel.jetbrainsindexmcpplugin.constants.ParamNames
import com.github.hechtcarmel.jetbrainsindexmcpplugin.constants.ToolNames
import com.github.hechtcarmel.jetbrainsindexmcpplugin.server.models.ToolCallResult
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.AbstractMcpTool
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.models.EvaluateResult
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.schema.SchemaBuilder
import com.intellij.openapi.project.Project
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

class EvaluateTool : AbstractMcpTool() {

    override val name = ToolNames.EVALUATE_EXPRESSION

    override val description = """
        在当前调试上下文中求值表达式。
        可以计算变量值、方法调用等。
    """.trimIndent()

    override val inputSchema: JsonObject = SchemaBuilder.tool()
        .projectPath()
        .stringProperty(ParamNames.EXPRESSION, "要计算的表达式", required = true)
        .stringProperty(ParamNames.SESSION_ID, "会话 ID（可选，默认当前会话）", required = false)
        .build()

    override suspend fun doExecute(project: Project, arguments: JsonObject): ToolCallResult {
        if (!isDebuggerSupported()) {
            return createDebuggerNotSupportedResult()
        }

        val expression = arguments[ParamNames.EXPRESSION]?.jsonPrimitive?.content
            ?: return createErrorResult("缺少必需参数: ${ParamNames.EXPRESSION}")

        val sessionId = arguments[ParamNames.SESSION_ID]?.jsonPrimitive?.content
        val session = resolveSession(project, sessionId)
            ?: return createSessionNotFoundResult(sessionId)

        if (!session.isPaused) {
            return createSessionNotPausedResult()
        }

        return edtAction {
            val evaluator = session.currentStackFrame?.evaluator
                ?: return@edtAction createErrorResult("无法获取表达式求值器")

            var result: EvaluateResult? = null

            evaluator.evaluate(expression, object : XDebuggerEvaluator.XEvaluationCallback {
                override fun evaluated(resultValue: com.intellij.xdebugger.frame.XValue) {
                    result = EvaluateResult(
                        success = true,
                        expression = expression,
                        result = resultValue.value,
                        type = resultValue.type
                    )
                }

                override fun errorOccurred(errorMessage: String) {
                    result = EvaluateResult(
                        success = false,
                        expression = expression,
                        error = errorMessage
                    )
                }
            }, null)

            // Wait for result with timeout
            var attempts = 0
            while (result == null && attempts < 100) {
                Thread.sleep(50)
                attempts++
            }

            result?.let { createJsonResult(it) }
                ?: createErrorResult("表达式求值超时")
        }
    }
}