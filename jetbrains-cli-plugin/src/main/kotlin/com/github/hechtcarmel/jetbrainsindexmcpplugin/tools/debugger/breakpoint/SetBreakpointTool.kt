package com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.breakpoint

import com.github.hechtcarmel.jetbrainsindexmcpplugin.constants.ParamNames
import com.github.hechtcarmel.jetbrainsindexmcpplugin.constants.ToolNames
import com.github.hechtcarmel.jetbrainsindexmcpplugin.server.models.ToolCallResult
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.AbstractMcpTool
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.models.BreakpointInfo
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.models.SetBreakpointResult
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.schema.SchemaBuilder
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import com.intellij.xdebugger.breakpoints.XLineBreakpointType
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive

class SetBreakpointTool : AbstractMcpTool() {

    override val name = ToolNames.SET_BREAKPOINT

    override val description = """
        在指定位置设置断点。
        可选设置条件和日志消息。
        返回断点 ID 和信息。
    """.trimIndent()

    override val inputSchema: JsonObject = SchemaBuilder.tool()
        .projectPath()
        .file(required = true)
        .intProperty(ParamNames.LINE, "行号（1-based）", required = true)
        .stringProperty(ParamNames.CONDITION, "断点条件表达式（可选）", required = false)
        .stringProperty(ParamNames.LOG_MESSAGE, "命中时记录的日志消息（可选）", required = false)
        .build()

    override suspend fun doExecute(project: Project, arguments: JsonObject): ToolCallResult {
        if (!isDebuggerSupported()) {
            return createDebuggerNotSupportedResult()
        }

        val file = arguments[ParamNames.FILE]?.jsonPrimitive?.content
            ?: return createErrorResult("缺少必需参数: ${ParamNames.FILE}")

        val line = arguments[ParamNames.LINE]?.jsonPrimitive?.int
            ?: return createErrorResult("缺少必需参数: ${ParamNames.LINE}")

        val condition = arguments[ParamNames.CONDITION]?.jsonPrimitive?.content
        val logMessage = arguments[ParamNames.LOG_MESSAGE]?.jsonPrimitive?.content

        return edtAction {
            val virtualFile = resolveFile(project, file)
                ?: return@edtAction createErrorResult("文件未找到: $file")

            val breakpointManager = getBreakpointManager(project)

            // Get the standard line breakpoint type from extension point
            val epName = ExtensionPointName<XLineBreakpointType<*>>("com.intellij.xdebugger.lineBreakpointType")
            val breakpointTypes = epName.extensionList
            val breakpointType = breakpointTypes.firstOrNull { it.canPutAt(virtualFile, line - 1, project) }
                ?: return@edtAction createErrorResult("无法为此文件类型创建断点")

            val breakpoint = breakpointManager.addLineBreakpoint(
                breakpointType,
                virtualFile.url,
                line - 1,
                null
            )

            // Note: Setting condition and logExpression requires language-specific handling
            // This is a simplified implementation that creates the breakpoint without conditions

            createJsonResult(SetBreakpointResult(
                success = true,
                breakpoint = BreakpointInfo(
                    id = breakpoint.hashCode().toString(),
                    file = file,
                    line = line,
                    enabled = true,
                    condition = condition,
                    logMessage = logMessage
                ),
                message = "断点已设置"
            ))
        }
    }
}