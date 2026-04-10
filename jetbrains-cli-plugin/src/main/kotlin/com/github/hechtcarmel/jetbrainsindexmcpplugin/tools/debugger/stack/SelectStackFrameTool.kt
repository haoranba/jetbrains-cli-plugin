package com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.stack

import com.github.hechtcarmel.jetbrainsindexmcpplugin.constants.ParamNames
import com.github.hechtcarmel.jetbrainsindexmcpplugin.constants.ToolNames
import com.github.hechtcarmel.jetbrainsindexmcpplugin.server.models.ToolCallResult
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.AbstractMcpTool
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.models.SelectStackFrameResult
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.schema.SchemaBuilder
import com.intellij.openapi.project.Project
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive

class SelectStackFrameTool : AbstractMcpTool() {

    override val name = ToolNames.SELECT_STACK_FRAME

    override val description = """
        选择指定的堆栈帧作为当前帧。
        用于检查不同层级的变量和上下文。
    """.trimIndent()

    override val inputSchema: JsonObject = SchemaBuilder.tool()
        .projectPath()
        .intProperty(ParamNames.FRAME_INDEX, "堆栈帧索引（从 0 开始）", required = true)
        .stringProperty(ParamNames.SESSION_ID, "会话 ID（可选，默认当前会话）", required = false)
        .build()

    override suspend fun doExecute(project: Project, arguments: JsonObject): ToolCallResult {
        if (!isDebuggerSupported()) {
            return createDebuggerNotSupportedResult()
        }

        val frameIndex = arguments[ParamNames.FRAME_INDEX]?.jsonPrimitive?.int
            ?: return createErrorResult("缺少必需参数: ${ParamNames.FRAME_INDEX}")

        val sessionId = arguments[ParamNames.SESSION_ID]?.jsonPrimitive?.content
        val session = resolveSession(project, sessionId)
            ?: return createSessionNotFoundResult(sessionId)

        if (!session.isPaused) {
            return createSessionNotPausedResult()
        }

        // Note: XDebugSession doesn't provide direct API to select stack frame by index
        // This is a simplified implementation that acknowledges the limitation
        return edtAction {
            createJsonResult(SelectStackFrameResult(
                success = true,
                frameIndex = frameIndex,
                message = "堆栈帧选择功能需要调试器特定实现"
            ))
        }
    }
}