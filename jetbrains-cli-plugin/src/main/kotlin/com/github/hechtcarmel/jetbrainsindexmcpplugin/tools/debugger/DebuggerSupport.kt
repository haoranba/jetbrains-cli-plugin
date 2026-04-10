package com.github.hechtcarmel.jetbrainsindexmcpplugin.tools.debugger

import com.github.hechtcarmel.jetbrainsindexmcpplugin.util.PluginDetectors

/**
 * Utility object for checking debugger support in the current IDE.
 */
object DebuggerSupport {
    /**
     * Returns true if the IDE supports debugging functionality.
     * Most JetBrains IDEs support this via the XDebugger API.
     */
    fun isSupported(): Boolean = PluginDetectors.debugger.isAvailable

    /**
     * Error message returned when debugger is not supported.
     */
    const val NOT_SUPPORTED_MESSAGE = "调试功能在当前 IDE 中不可用。请使用支持调试的 JetBrains IDE（如 IntelliJ IDEA、PyCharm、GoLand 等）。"
}