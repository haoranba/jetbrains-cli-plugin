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
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

class GetVariablesTool : AbstractMcpTool() {

    override val name = ToolNames.GET_VARIABLES

    override val description = """
        获取当前堆栈帧的变量。
        包括局部变量、参数等。
    """.trimIndent()

    override val inputSchema: JsonObject = SchemaBuilder.tool()
        .projectPath()
        .stringProperty(ParamNames.SESSION_ID, "会话 ID（可选，默认当前会话）", required = false)
        .stringProperty("scope", "变量作用域（locals/arguments/all，默认 all）", required = false)
        .build()

    override suspend fun doExecute(project: Project, arguments: JsonObject): ToolCallResult {
        if (!isDebuggerSupported()) {
            return createDebuggerNotSupportedResult()
        }

        val sessionId = arguments[ParamNames.SESSION_ID]?.jsonPrimitive?.content
        val scope = arguments["scope"]?.jsonPrimitive?.content ?: "all"

        val session = resolveSession(project, sessionId)
            ?: return createSessionNotFoundResult(sessionId)

        if (!session.isPaused) {
            return createSessionNotPausedResult()
        }

        return readAction {
            val currentFrame = session.currentStackFrame
                ?: return@readAction createErrorResult("没有当前堆栈帧")

            val variables = mutableListOf<VariableInfo>()
            var completed = false

            currentFrame.computeChildren(object : XCompositeNode() {
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
                    if (last) completed = true
                }

                override fun tooManyChildren(remaining: Int) {
                    // Handle too many children
                }

                override fun setErrorMessage(message: String) {
                    // Handle error
                }
            })

            createJsonResult(GetVariablesResult(
                variables = variables,
                scope = scope
            ))
        }
    }
}