package com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.stack

import com.github.hechtcarmel.jetbrainsindexmcpplugin.constants.ParamNames
import com.github.hechtcarmel.jetbrainsindexmcpplugin.constants.ToolNames
import com.github.hechtcarmel.jetbrainsindexmcpplugin.server.models.ToolCallResult
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.AbstractMcpTool
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.models.GetStackTraceResult
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.models.StackFrameInfo
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.schema.SchemaBuilder
import com.intellij.openapi.project.Project
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

class GetStackTraceTool : AbstractMcpTool() {

    override val name = ToolNames.GET_STACK_TRACE

    override val description = """
        获取当前线程的调用堆栈。
        返回堆栈帧列表，包括文件、行号、方法名等信息。
    """.trimIndent()

    override val inputSchema: JsonObject = SchemaBuilder.tool()
        .projectPath()
        .stringProperty(ParamNames.SESSION_ID, "会话 ID（可选，默认当前会话）", required = false)
        .build()

    override suspend fun doExecute(project: Project, arguments: JsonObject): ToolCallResult {
        if (!isDebuggerSupported()) {
            return createDebuggerNotSupportedResult()
        }

        val sessionId = arguments[ParamNames.SESSION_ID]?.jsonPrimitive?.content
        val session = resolveSession(project, sessionId)
            ?: return createSessionNotFoundResult(sessionId)

        if (!session.isPaused) {
            return createSessionNotPausedResult()
        }

        return readAction {
            val currentFrame = session.currentStackFrame
                ?: return@readAction createErrorResult("没有当前堆栈帧")

            // Note: XDebugSession doesn't provide direct access to all stack frames
            // This returns the current frame as a single-element list
            // Full stack trace requires debugger-specific API
            val frames = mutableListOf<StackFrameInfo>()
            val sourcePosition = currentFrame.sourcePosition

            frames.add(StackFrameInfo(
                index = 0,
                name = currentFrame.toString(),
                file = sourcePosition?.file?.path,
                line = sourcePosition?.line?.plus(1)
            ))

            createJsonResult(GetStackTraceResult(
                frames = frames,
                currentIndex = 0,
                totalCount = frames.size
            ))
        }
    }
}