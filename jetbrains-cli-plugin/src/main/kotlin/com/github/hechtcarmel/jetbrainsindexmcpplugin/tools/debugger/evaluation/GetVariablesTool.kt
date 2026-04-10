package com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.evaluation

import com.github.hechtcarmel.jetbrainsindexmcpplugin.constants.ParamNames
import com.github.hechtcarmel.jetbrainsindexmcpplugin.constants.ToolNames
import com.github.hechtcarmel.jetbrainsindexmcpplugin.server.models.ToolCallResult
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.AbstractMcpTool
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.models.GetVariablesResult
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.models.VariableInfo
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.schema.SchemaBuilder
import com.intellij.openapi.project.Project
import com.intellij.xdebugger.frame.XCompositeNode
import com.intellij.xdebugger.frame.XValueChildrenList
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

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

    private suspend fun getVariablesAsync(
        frame: com.intellij.xdebugger.frame.XStackFrame
    ): List<VariableInfo> = suspendCancellableCoroutine { continuation ->
        val variables = mutableListOf<VariableInfo>()

        frame.computeChildren(object : XCompositeNode() {
            override fun addChildren(children: XValueChildrenList, last: Boolean) {
                for (i in 0 until children.size()) {
                    val name = children.getName(i)
                    val value = children.getValue(i)
                    variables.add(VariableInfo(
                        name = name,
                        value = value.value ?: "null",
                        type = value.type,
                        hasChildren = value.canNavigateToSourceChildren()
                    ))
                }
                if (last && continuation.isActive) {
                    continuation.resume(variables)
                }
            }

            override fun tooManyChildren(remaining: Int) {
                // Still return what we have, but note truncation
                if (continuation.isActive) {
                    continuation.resume(variables)
                }
            }

            override fun setErrorMessage(message: String) {
                if (continuation.isActive) {
                    continuation.resumeWithException(Exception(message))
                }
            }
        })

        continuation.invokeOnCancellation {
            // Handle cancellation if needed
        }
    }
}