package com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.session

import com.github.hechtcarmel.jetbrainsindexmcpplugin.constants.ToolNames
import com.github.hechtcarmel.jetbrainsindexmcpplugin.server.models.ToolCallResult
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.AbstractMcpTool
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.DebuggerSupport
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.models.ListRunConfigurationsResult
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.models.RunConfigurationInfo
import com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.schema.SchemaBuilder
import com.intellij.execution.RunManager
import com.intellij.openapi.project.Project
import kotlinx.serialization.json.JsonObject

class ListRunConfigurationsTool : AbstractMcpTool() {

    override val name = ToolNames.LIST_RUN_CONFIGURATIONS

    override val description = """
        列出项目中所有可用的运行配置。
        运行配置定义了如何运行或调试应用程序。
        返回配置名称和类型列表。
    """.trimIndent()

    override val inputSchema: JsonObject = SchemaBuilder.tool()
        .projectPath()
        .build()

    override suspend fun doExecute(project: Project, arguments: JsonObject): ToolCallResult {
        if (!isDebuggerSupported()) {
            return createDebuggerNotSupportedResult()
        }

        return readAction {
            val runManager = RunManager.getInstance(project)
            val configurations = runManager.allConfigurationsList.map { config ->
                RunConfigurationInfo(
                    name = config.name,
                    type = config.type.id,
                    typeName = config.type.displayName
                )
            }

            createJsonResult(ListRunConfigurationsResult(
                configurations = configurations,
                totalCount = configurations.size
            ))
        }
    }
}