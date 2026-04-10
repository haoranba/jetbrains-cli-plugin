package com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.models

import kotlinx.serialization.Serializable

@Serializable
data class VariableInfo(
    val name: String,
    val value: String,
    val type: String? = null,
    val hasChildren: Boolean = false,
    val children: List<VariableInfo>? = null
)

@Serializable
data class GetVariablesResult(
    val variables: List<VariableInfo>,
    val scope: String? = null
)

@Serializable
data class SetVariableResult(
    val success: Boolean,
    val name: String,
    val newValue: String,
    val message: String? = null
)