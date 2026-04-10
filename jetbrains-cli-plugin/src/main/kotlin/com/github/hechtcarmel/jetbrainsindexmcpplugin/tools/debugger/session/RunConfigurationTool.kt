package com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.session

import com.github.hechtcarmel.jetbrainsindexmcpplugin.constants.ParamNames
import com.github.hechtcarmel.jetbrainsindexmcpplugin.constants.ToolNames
import com.github.hechtcarmel.jetbrainsindexmcpplugin.server.models.ToolCallResult
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.AbstractMcpTool
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.models.ExecuteRunConfigurationResult
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.schema.SchemaBuilder
import com.intellij.execution.RunManager
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.ExecutionEnvironmentBuilder
import com.intellij.openapi.project.Project
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

class RunConfigurationTool : AbstractMcpTool() {

    override val name = ToolNames.EXECUTE_RUN_CONFIGURATION

    override val description = """
        执行指定的运行配置。
        可以选择运行模式或调试模式。
        调试模式会启动调试会话。
        返回执行结果和可能的会话 ID。
    """.trimIndent()

    override val inputSchema: JsonObject = SchemaBuilder.tool()
        .projectPath()
        .stringProperty(ParamNames.CONFIG_NAME, "运行配置名称", required = true)
        .booleanProperty("debug", "是否以调试模式运行（默认 true）", required = false)
        .build()

    override suspend fun doExecute(project: Project, arguments: JsonObject): ToolCallResult {
        if (!isDebuggerSupported()) {
            return createDebuggerNotSupportedResult()
        }

        val configName = arguments[ParamNames.CONFIG_NAME]?.jsonPrimitive?.content
            ?: return createErrorResult("缺少必需参数: ${ParamNames.CONFIG_NAME}")

        val debug = arguments["debug"]?.jsonPrimitive?.content?.toBoolean() ?: true

        return readAction {
            val runManager = RunManager.getInstance(project)
            val settings = runManager.findConfigurationByName(configName)
                ?: return@readAction createErrorResult("未找到运行配置: $configName")

            val executor = if (debug) DefaultDebugExecutor.getDebugExecutorInstance()
                          else DefaultRunExecutor.getRunExecutorInstance()

            val builder = ExecutionEnvironmentBuilder.createOrNull(executor, settings)
                ?: return@readAction createErrorResult("无法创建执行环境")

            try {
                val environment = builder.build()
                val sessionId = if (debug) {
                    environment.executionId.toString()
                } else null

                environment.runner.execute(environment)

                createJsonResult(ExecuteRunConfigurationResult(
                    success = true,
                    sessionId = sessionId,
                    message = if (debug) "已启动调试会话" else "已启动运行"
                ))
            } catch (e: Exception) {
                createErrorResult("执行失败: ${e.message}")
            }
        }
    }
}