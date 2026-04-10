package com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger.models

import kotlinx.serialization.Serializable

@Serializable
data class EvaluateResult(
    val success: Boolean,
    val expression: String,
    val result: String? = null,
    val type: String? = null,
    val error: String? = null
)

@Serializable
data class SourceContextResult(
    val file: String,
    val currentLine: Int,
    val lines: List<SourceLine>,
    val highlightedRange: HighlightedRange? = null
)

@Serializable
data class SourceLine(
    val lineNumber: Int,
    val content: String,
    val isCurrentLine: Boolean
)

@Serializable
data class HighlightedRange(
    val startLine: Int,
    val startColumn: Int,
    val endLine: Int,
    val endColumn: Int
)