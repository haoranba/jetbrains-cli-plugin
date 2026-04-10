package com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.models

import kotlinx.serialization.Serializable

@Serializable
data class DebugSessionInfo(
    val id: String,
    val name: String,
    val state: String,
    val projectName: String,
    val runConfigName: String? = null
)

@Serializable
data class ListDebugSessionsResult(
    val sessions: List<DebugSessionInfo>,
    val totalCount: Int
)

@Serializable
data class StartDebugSessionResult(
    val success: Boolean,
    val sessionId: String? = null,
    val message: String
)

@Serializable
data class StopDebugSessionResult(
    val success: Boolean,
    val message: String
)

@Serializable
data class DebugSessionStatus(
    val sessionId: String,
    val state: String,
    val isPaused: Boolean,
    val currentFile: String? = null,
    val currentLine: Int? = null,
    val currentThreadName: String? = null
)

@Serializable
data class ExecutionResult(
    val success: Boolean,
    val sessionId: String,
    val state: String,
    val message: String? = null
)