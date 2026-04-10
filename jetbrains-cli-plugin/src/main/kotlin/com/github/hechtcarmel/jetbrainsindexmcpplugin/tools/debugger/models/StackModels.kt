package com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.models

import kotlinx.serialization.Serializable

@Serializable
data class StackFrameInfo(
    val index: Int,
    val name: String,
    val file: String? = null,
    val line: Int? = null,
    val packageName: String? = null,
    val className: String? = null,
    val methodName: String? = null
)

@Serializable
data class GetStackTraceResult(
    val frames: List<StackFrameInfo>,
    val currentIndex: Int,
    val totalCount: Int
)

@Serializable
data class ThreadInfo(
    val id: Long,
    val name: String,
    val state: String,
    val isCurrent: Boolean
)

@Serializable
data class ListThreadsResult(
    val threads: List<ThreadInfo>,
    val currentThreadId: Long? = null,
    val totalCount: Int
)

@Serializable
data class SelectStackFrameResult(
    val success: Boolean,
    val frameIndex: Int,
    val message: String? = null
)