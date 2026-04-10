package com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.evaluation

import com.github.hechtcarmel.jetbrainsindexmcpplugin.constants.ParamNames
import com.github.hechtcarmel.jetbrainsindexmcpplugin.constants.ToolNames
import com.github.hechtcarmel.jetbrainsindexmcpplugin.server.models.ToolCallResult
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.AbstractMcpTool
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.models.GetVariablesResult
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.models.VariableInfo
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.schema.SchemaBuilder
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.project.Project
import com.intellij.ui.SimpleTextAttributes
import com.intellij.xdebugger.frame.XCompositeNode
import com.intellij.xdebugger.frame.XDebuggerTreeNodeHyperlink
import com.intellij.xdebugger.frame.XFullValueEvaluator
import com.intellij.xdebugger.frame.XValue
import com.intellij.xdebugger.frame.XValueChildrenList
import com.intellij.xdebugger.frame.XValueNode
import com.intellij.xdebugger.frame.presentation.XValuePresentation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.swing.Icon
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Helper class to capture XValue presentation information with children flag
 */
private class CapturingXValueNodeWithChildren(
    private val onResult: (String?, String?, Boolean) -> Unit
) : XValueNode {

    override fun setPresentation(icon: Icon?, type: String?, value: String, hasChildren: Boolean) {
        onResult(value, type, hasChildren)
    }

    override fun setPresentation(icon: Icon?, presentation: XValuePresentation, hasChildren: Boolean) {
        val buffer = StringBuilder()
        presentation.renderValue(object : XValuePresentation.XValueTextRenderer {
            override fun renderValue(value: String) {
                buffer.append(value)
            }

            override fun renderValue(value: String, attributes: TextAttributesKey) {
                buffer.append(value)
            }

            override fun renderStringValue(value: String) {
                buffer.append(value)
            }

            override fun renderStringValue(value: String, additionalSpecialChars: String?, maxLength: Int) {
                buffer.append(value)
            }

            override fun renderKeywordValue(value: String) {
                buffer.append(value)
            }

            override fun renderError(value: String) {
                buffer.append(value)
            }

            override fun renderNumericValue(value: String) {
                buffer.append(value)
            }

            override fun renderComment(comment: String) {
                buffer.append(comment)
            }

            override fun renderSpecialSymbol(symbol: String) {
                buffer.append(symbol)
            }
        })
        onResult(buffer.toString(), null, hasChildren)
    }

    override fun setFullValueEvaluator(fullValueEvaluator: XFullValueEvaluator) {
        // No-op
    }
}

private data class XValueInfo(
    val value: String? = null,
    val type: String? = null,
    val hasChildren: Boolean = false
)

class GetVariablesTool : AbstractMcpTool() {

    override val name = ToolNames.GET_VARIABLES

    override val description = """
        获取当前堆栈帧的变量。
        包括局部变量、参数等。
    """.trimIndent()

    override val inputSchema: JsonObject = SchemaBuilder.tool()
        .projectPath()
        .stringProperty(ParamNames.SESSION_ID, "会话 ID（可选，默认当前会话）", required = false)
        .stringProperty(ParamNames.SCOPE, "变量作用域（locals/arguments/all，默认 all）", required = false)
        .build()

    override suspend fun doExecute(project: Project, arguments: JsonObject): ToolCallResult {
        if (!isDebuggerSupported()) {
            return createDebuggerNotSupportedResult()
        }

        val sessionId = arguments[ParamNames.SESSION_ID]?.jsonPrimitive?.content
        val scope = arguments[ParamNames.SCOPE]?.jsonPrimitive?.content ?: "all"

        val session = resolveSession(project, sessionId)
            ?: return createSessionNotFoundResult(sessionId)

        if (!session.isPaused) {
            return createSessionNotPausedResult()
        }

        val currentFrame = session.currentStackFrame
            ?: return createErrorResult("没有当前堆栈帧")

        return try {
            val variables = getVariablesAsync(currentFrame)

            createJsonResult(GetVariablesResult(
                variables = variables,
                scope = scope
            ))
        } catch (e: Exception) {
            createErrorResult("获取变量失败: ${e.message}")
        }
    }

    private fun extractXValueInfo(xValue: XValue): XValueInfo {
        var value: String? = null
        var type: String? = null
        var hasChildren = false

        xValue.computePresentation(
            CapturingXValueNodeWithChildren(onResult = { v, t, hc ->
                value = v
                type = t
                hasChildren = hc
            }),
            com.intellij.xdebugger.frame.XValuePlace.TREE
        )

        return XValueInfo(value, type, hasChildren)
    }

    private suspend fun getVariablesAsync(
        frame: com.intellij.xdebugger.frame.XStackFrame
    ): List<VariableInfo> = suspendCancellableCoroutine { continuation ->
        val variables = mutableListOf<VariableInfo>()

        frame.computeChildren(object : XCompositeNode {
            override fun addChildren(children: XValueChildrenList, last: Boolean) {
                for (i in 0 until children.size()) {
                    val name = children.getName(i)
                    val value = children.getValue(i)
                    val info = extractXValueInfo(value)
                    variables.add(VariableInfo(
                        name = name,
                        value = info.value ?: "null",
                        type = info.type,
                        hasChildren = info.hasChildren
                    ))
                }
                if (last && continuation.isActive) {
                    continuation.resume(variables)
                }
            }

            override fun tooManyChildren(remaining: Int) {
                if (continuation.isActive) {
                    continuation.resume(variables)
                }
            }

            override fun setErrorMessage(message: String) {
                if (continuation.isActive) {
                    continuation.resumeWithException(Exception(message))
                }
            }

            override fun setErrorMessage(message: String, hyperlink: XDebuggerTreeNodeHyperlink?) {
                if (continuation.isActive) {
                    continuation.resumeWithException(Exception(message))
                }
            }

            override fun setAlreadySorted(alreadySorted: Boolean) {
                // No-op
            }

            override fun setMessage(message: String, icon: Icon?, attributes: SimpleTextAttributes, hyperlink: XDebuggerTreeNodeHyperlink?) {
                // No-op
            }
        })

        continuation.invokeOnCancellation {
            // Handle cancellation if needed
        }
    }
}