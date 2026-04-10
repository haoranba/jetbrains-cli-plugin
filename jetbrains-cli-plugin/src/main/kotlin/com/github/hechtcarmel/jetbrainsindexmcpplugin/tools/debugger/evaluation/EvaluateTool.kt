package com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.evaluation

import com.github.hechtcarmel.jetbrainsindexmcpplugin.constants.ParamNames
import com.github.hechtcarmel.jetbrainsindexmcpplugin.constants.ToolNames
import com.github.hechtcarmel.jetbrainsindexmcpplugin.server.models.ToolCallResult
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.AbstractMcpTool
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.models.EvaluateResult
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.schema.SchemaBuilder
import com.intellij.openapi.project.Project
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.coroutines.resume

class EvaluateTool : AbstractMcpTool() {

    override val name = ToolNames.EVALUATE_EXPRESSION

    override val description = """
        在当前调试上下文中求值表达式。
        可以计算变量值、方法调用等。
        超时时间：5秒。
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

        val evaluator = session.currentStackFrame?.evaluator
            ?: return createErrorResult("无法获取表达式求值器")

        return try {
            val result = withTimeout(5000) {
                evaluateAsync(evaluator, expression)
            }
            createJsonResult(result)
        } catch (e: Exception) {
            createErrorResult("表达式求值失败: ${e.message}")
        }
    }

    private suspend fun evaluateAsync(
        evaluator: XDebuggerEvaluator,
        expression: String
    ): EvaluateResult = suspendCancellableCoroutine { continuation ->
        evaluator.evaluate(expression, object : XDebuggerEvaluator.XEvaluationCallback {
            override fun evaluated(resultValue: com.intellij.xdebugger.frame.XValue) {
                if (continuation.isActive) {
                    continuation.resume(EvaluateResult(
                        success = true,
                        expression = expression,
                        result = resultValue.value,
                        type = resultValue.type
                    ))
                }
            }

            override fun errorOccurred(errorMessage: String) {
                if (continuation.isActive) {
                    continuation.resume(EvaluateResult(
                        success = false,
                        expression = expression,
                        error = errorMessage
                    ))
                }
            }
        }, null)
    }
}