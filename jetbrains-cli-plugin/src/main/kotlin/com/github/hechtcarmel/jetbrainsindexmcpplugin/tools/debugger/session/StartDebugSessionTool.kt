package com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.session

import com.github.hechtcarmel.jetbrainsindexmcpplugin.constants.ParamNames
import com.github.hechtcarmel.jetbrainsindexmcpplugin.constants.ToolNames
import com.github.hechtcarmel.jetbrainsindexmcpplugin.server.models.ToolCallResult
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.AbstractMcpTool
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.models.StartDebugSessionResult
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.schema.SchemaBuilder
import com.intellij.execution.RunManager
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.execution.runners.ExecutionEnvironmentBuilder
import com.intellij.openapi.project.Project
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

class StartDebugSessionTool : AbstractMcpTool() {

    override val name = ToolNames.START_DEBUG_SESSION

    override val description = """
        启动一个新的调试会话。
        需要指定运行配置名称。
        返回新会话的 ID。
    """.trimIndent()

    override val inputSchema: JsonObject = SchemaBuilder.tool()
        .projectPath()
        .stringProperty(ParamNames.CONFIG_NAME, "运行配置名称", required = true)
        .build()

    override suspend fun doExecute(project: Project, arguments: JsonObject): ToolCallResult {
        if (!isDebuggerSupported()) {
            return createDebuggerNotSupportedResult()
        }

        val configName = arguments[ParamNames.CONFIG_NAME]?.jsonPrimitive?.content
            ?: return createErrorResult("缺少必需参数: ${ParamNames.CONFIG_NAME}")

        return readAction {
            val runManager = RunManager.getInstance(project)
            val settings = runManager.findConfigurationByName(configName)
                ?: return@readAction createErrorResult("未找到运行配置: $configName")

            val executor = DefaultDebugExecutor.getDebugExecutorInstance()
            val builder = ExecutionEnvironmentBuilder.createOrNull(executor, settings)
                ?: return@readAction createErrorResult("无法创建执行环境")

            try {
                val environment = builder.build()
                val sessionId = environment.executionId.toString()
                environment.runner.execute(environment)

                createJsonResult(StartDebugSessionResult(
                    success = true,
                    sessionId = sessionId,
                    message = "调试会话已启动"
                ))
            } catch (e: Exception) {
                createErrorResult("启动调试会话失败: ${e.message}")
            }
        }
    }
}