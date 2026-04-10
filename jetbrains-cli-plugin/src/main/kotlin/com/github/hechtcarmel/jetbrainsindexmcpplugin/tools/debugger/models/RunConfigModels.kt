package com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.models

import kotlinx.serialization.Serializable

@Serializable
data class RunConfigurationInfo(
    val name: String,
    val type: String,
    val typeName: String? = null
)

@Serializable
data class ListRunConfigurationsResult(
    val configurations: List<RunConfigurationInfo>,
    val totalCount: Int
)

@Serializable
data class ExecuteRunConfigurationResult(
    val success: Boolean,
    val sessionId: String? = null,
    val message: String
)