package com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.session

import com.github.hechtcarmel.jetbrainsindexmcpplugin.constants.ToolNames
import com.github.hechtcarmel.jetbrainsindexmcpplugin.server.models.ToolCallResult
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.AbstractMcpTool
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.models.DebugSessionInfo
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.models.ListDebugSessionsResult
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.schema.SchemaBuilder
import com.intellij.openapi.project.Project
import kotlinx.serialization.json.JsonObject

class ListDebugSessionsTool : AbstractMcpTool() {

    override val name = ToolNames.LIST_DEBUG_SESSIONS

    override val description = """
        列出所有活动的调试会话。
        返回会话 ID、名称、状态等信息。
    """.trimIndent()

    override val inputSchema: JsonObject = SchemaBuilder.tool()
        .projectPath()
        .build()

    override suspend fun doExecute(project: Project, arguments: JsonObject): ToolCallResult {
        if (!isDebuggerSupported()) {
            return createDebuggerNotSupportedResult()
        }

        return readAction {
            val sessions = getAllSessions(project).map { session ->
                DebugSessionInfo(
                    id = getSessionId(session),
                    name = session.sessionName,
                    state = getSessionState(session),
                    projectName = session.project.name,
                    runConfigName = session.runProfile?.name
                )
            }

            createJsonResult(ListDebugSessionsResult(
                sessions = sessions,
                totalCount = sessions.size
            ))
        }
    }

    private fun getSessionState(session: com.intellij.xdebugger.XDebugSession): String {
        return when {
            session.isStopped -> "STOPPED"
            session.isPaused -> "PAUSED"
            session.isSuspended -> "SUSPENDED"
            else -> "RUNNING"
        }
    }
}