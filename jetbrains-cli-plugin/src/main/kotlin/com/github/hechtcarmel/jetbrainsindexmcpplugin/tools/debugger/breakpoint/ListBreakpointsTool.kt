package com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.breakpoint

import com.github.hechtcarmel.jetbrainsindexmcpplugin.constants.ToolNames
import com.github.hechtcarmel.jetbrainsindexmcpplugin.server.models.ToolCallResult
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.AbstractMcpTool
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.models.BreakpointInfo
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.models.ListBreakpointsResult
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.schema.SchemaBuilder
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import kotlinx.serialization.json.JsonObject

class ListBreakpointsTool : AbstractMcpTool() {

    override val name = ToolNames.LIST_BREAKPOINTS

    override val description = """
        列出项目中所有断点。
        返回断点位置、条件、日志消息等信息。
    """.trimIndent()

    override val inputSchema: JsonObject = SchemaBuilder.tool()
        .projectPath()
        .build()

    override suspend fun doExecute(project: Project, arguments: JsonObject): ToolCallResult {
        if (!isDebuggerSupported()) {
            return createDebuggerNotSupportedResult()
        }

        return readAction {
            val breakpointManager = getBreakpointManager(project)
            val breakpoints = mutableListOf<BreakpointInfo>()

            for (breakpoint in breakpointManager.allBreakpoints) {
                if (breakpoint is XLineBreakpoint<*>) {
                    val fileUrl = breakpoint.fileUrl
                    val virtualFile = fileUrl?.let { VirtualFileManager.getInstance().findFileByUrl(it) }
                    val filePath = virtualFile?.let { getRelativePath(project, it) }
                        ?: fileUrl
                        ?: continue

                    breakpoints.add(BreakpointInfo(
                        id = breakpoint.hashCode().toString(),
                        file = filePath,
                        line = breakpoint.line + 1,
                        enabled = breakpoint.isEnabled,
                        condition = breakpoint.conditionExpression?.expression,
                        logMessage = breakpoint.logExpressionObject?.expression
                    ))
                }
            }

            createJsonResult(ListBreakpointsResult(
                breakpoints = breakpoints,
                totalCount = breakpoints.size
            ))
        }
    }
}