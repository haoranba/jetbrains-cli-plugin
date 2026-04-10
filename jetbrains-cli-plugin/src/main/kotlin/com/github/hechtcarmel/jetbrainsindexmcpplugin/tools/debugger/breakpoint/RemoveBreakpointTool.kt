package com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.breakpoint

import com.github.hechtcarmel.jetbrainsindexmcpplugin.constants.ParamNames
import com.github.hechtcarmel.jetbrainsindexmcpplugin.constants.ToolNames
import com.github.hechtcarmel.jetbrainsindexmcpplugin.server.models.ToolCallResult
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.AbstractMcpTool
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.models.RemoveBreakpointResult
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.schema.SchemaBuilder
import com.intellij.openapi.project.Project
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

class RemoveBreakpointTool : AbstractMcpTool() {

    override val name = ToolNames.REMOVE_BREAKPOINT

    override val description = """
        移除指定的断点。
        需要提供断点 ID。
    """.trimIndent()

    override val inputSchema: JsonObject = SchemaBuilder.tool()
        .projectPath()
        .stringProperty(ParamNames.BREAKPOINT_ID, "断点 ID", required = true)
        .build()

    override suspend fun doExecute(project: Project, arguments: JsonObject): ToolCallResult {
        if (!isDebuggerSupported()) {
            return createDebuggerNotSupportedResult()
        }

        val breakpointId = arguments[ParamNames.BREAKPOINT_ID]?.jsonPrimitive?.content
            ?: return createErrorResult("缺少必需参数: ${ParamNames.BREAKPOINT_ID}")

        return edtAction {
            val breakpointManager = getBreakpointManager(project)

            val breakpoint = breakpointManager.allBreakpoints.find {
                it.hashCode().toString() == breakpointId
            } ?: return@edtAction createErrorResult("未找到断点: $breakpointId")

            breakpointManager.removeBreakpoint(breakpoint)

            createJsonResult(RemoveBreakpointResult(
                success = true,
                message = "断点已移除"
            ))
        }
    }
}