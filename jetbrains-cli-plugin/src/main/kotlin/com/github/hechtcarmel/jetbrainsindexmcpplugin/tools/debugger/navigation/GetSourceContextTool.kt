package com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.navigation

import com.github.hechtcarmel.jetbrainsindexmcpplugin.constants.ParamNames
import com.github.hechtcarmel.jetbrainsindexmcpplugin.constants.ToolNames
import com.github.hechtcarmel.jetbrainsindexmcpplugin.server.models.ToolCallResult
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.AbstractMcpTool
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.models.SourceContextResult
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.models.SourceLine
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.schema.SchemaBuilder
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiManager
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive

class GetSourceContextTool : AbstractMcpTool() {

    override val name = ToolNames.GET_SOURCE_CONTEXT

    override val description = """
        获取当前执行位置的源码上下文。
        返回当前行周围的代码，便于理解执行位置。
    """.trimIndent()

    override val inputSchema: JsonObject = SchemaBuilder.tool()
        .projectPath()
        .stringProperty(ParamNames.SESSION_ID, "会话 ID（可选，默认当前会话）", required = false)
        .intProperty(ParamNames.CONTEXT_LINES, "上下文行数（默认 5）", required = false)
        .build()

    override suspend fun doExecute(project: Project, arguments: JsonObject): ToolCallResult {
        if (!isDebuggerSupported()) {
            return createDebuggerNotSupportedResult()
        }

        val sessionId = arguments[ParamNames.SESSION_ID]?.jsonPrimitive?.content
        val contextLines = arguments[ParamNames.CONTEXT_LINES]?.jsonPrimitive?.int ?: 5

        val session = resolveSession(project, sessionId)
            ?: return createSessionNotFoundResult(sessionId)

        if (!session.isPaused) {
            return createSessionNotPausedResult()
        }

        return readAction {
            val currentFrame = session.currentStackFrame
                ?: return@readAction createErrorResult("没有当前堆栈帧")

            val sourcePosition = currentFrame.sourcePosition
                ?: return@readAction createErrorResult("无法获取源码位置")

            val file = sourcePosition.file
            val currentLine = sourcePosition.line // 0-based

            val psiFile = PsiManager.getInstance(project).findFile(file)
                ?: return@readAction createErrorResult("无法获取 PSI 文件")

            val document = PsiDocumentManager.getInstance(project).getDocument(psiFile)
                ?: return@readAction createErrorResult("无法获取文档")

            val startLine = maxOf(0, currentLine - contextLines)
            val endLine = minOf(document.lineCount - 1, currentLine + contextLines)

            val lines = (startLine..endLine).map { lineIndex ->
                val lineStart = document.getLineStartOffset(lineIndex)
                val lineEnd = document.getLineEndOffset(lineIndex)
                val content = document.getText(TextRange(lineStart, lineEnd))

                SourceLine(
                    lineNumber = lineIndex + 1, // 1-based
                    content = content,
                    isCurrentLine = lineIndex == currentLine
                )
            }

            createJsonResult(SourceContextResult(
                file = getRelativePath(project, file),
                currentLine = currentLine + 1, // 1-based
                lines = lines
            ))
        }
    }
}