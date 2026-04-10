package com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.models

import kotlinx.serialization.Serializable

@Serializable
data class BreakpointInfo(
    val id: String,
    val file: String,
    val line: Int,
    val enabled: Boolean,
    val condition: String? = null,
    val logMessage: String? = null,
    val hitCount: Int? = null
)

@Serializable
data class ListBreakpointsResult(
    val breakpoints: List<BreakpointInfo>,
    val totalCount: Int
)

@Serializable
data class SetBreakpointResult(
    val success: Boolean,
    val breakpoint: BreakpointInfo? = null,
    val message: String? = null
)

@Serializable
data class RemoveBreakpointResult(
    val success: Boolean,
    val message: String
)