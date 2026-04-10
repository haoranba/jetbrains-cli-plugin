package com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.execution

import com.github.hechtcarmel.jetbrainsindexmcpplugin.constants.ParamNames
import com.github.hechtcarmel.jetbrainsindexmcpplugin.constants.ToolNames
import com.github.hechtcarmel.jetbrainsindexmcpplugin.server.models.ToolCallResult
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.AbstractMcpTool
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.models.ExecutionResult
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.schema.SchemaBuilder
import com.intellij.openapi.project.Project
import com.intellij.xdebugger.XDebuggerUtil
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive

class RunToLineTool : AbstractMcpTool() {

    override val name = ToolNames.RUN_TO_LINE

    override val description = """
        运行到指定行（Run to Cursor）。
        从当前位置运行到指定的文件和行号。
    """.trimIndent()

    override val inputSchema: JsonObject = SchemaBuilder.tool()
        .projectPath()
        .stringProperty(ParamNames.SESSION_ID, "会话 ID（可选，默认当前会话）", required = false)
        .file(required = true)
        .intProperty(ParamNames.LINE, "目标行号（1-based）", required = true)
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

        val file = arguments[ParamNames.FILE]?.jsonPrimitive?.content
            ?: return createErrorResult("缺少必需参数: ${ParamNames.FILE}")

        val line = arguments[ParamNames.LINE]?.jsonPrimitive?.int
            ?: return createErrorResult("缺少必需参数: ${ParamNames.LINE}")

        return edtAction {
            try {
                val virtualFile = resolveFile(project, file)
                    ?: return@edtAction createErrorResult("文件未找到: $file")

                val position = XDebuggerUtil.getInstance().createPosition(virtualFile, line - 1)
                    ?: return@edtAction createErrorResult("无法创建源码位置: $file:$line")

                session.runToPosition(position, false)
                createJsonResult(ExecutionResult(
                    success = true,
                    sessionId = getSessionId(session),
                    state = getSessionState(session),
                    message = "正在运行到 $file:$line"
                ))
            } catch (e: Exception) {
                createErrorResult("运行到行失败: ${e.message}")
            }
        }
    }
}