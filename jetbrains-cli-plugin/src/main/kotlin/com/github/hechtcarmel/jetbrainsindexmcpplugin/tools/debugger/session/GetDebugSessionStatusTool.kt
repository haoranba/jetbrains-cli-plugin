package com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.session

import com.github.hechtcarmel.jetbrainsindexmcpplugin.constants.ParamNames
import com.github.hechtcarmel.jetbrainsindexmcpplugin.constants.ToolNames
import com.github.hechtcarmel.jetbrainsindexmcpplugin.server.models.ToolCallResult
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.AbstractMcpTool
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.models.DebugSessionStatus
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.schema.SchemaBuilder
import com.intellij.openapi.project.Project
import com.intellij.xdebugger.XDebugSession
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

class GetDebugSessionStatusTool : AbstractMcpTool() {

    override val name = ToolNames.GET_DEBUG_SESSION_STATUS

    override val description = """
        获取调试会话的当前状态。
        包括是否暂停、当前位置、当前线程等信息。
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

        return readAction {
            createJsonResult(getSessionStatus(session))
        }
    }

    private fun getSessionStatus(session: XDebugSession): DebugSessionStatus {
        val currentFrame = session.currentStackFrame
        val currentFile = currentFrame?.sourcePosition?.file?.path
        val currentLine = currentFrame?.sourcePosition?.line

        return DebugSessionStatus(
            sessionId = getSessionId(session),
            state = getSessionState(session),
            isPaused = session.isPaused,
            currentFile = currentFile,
            currentLine = currentLine?.plus(1),
            currentThreadName = null // Thread name not directly available
        )
    }
}