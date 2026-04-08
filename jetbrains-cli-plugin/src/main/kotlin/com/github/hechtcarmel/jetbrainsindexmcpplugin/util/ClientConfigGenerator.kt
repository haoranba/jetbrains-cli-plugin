package com.github.hechtcarmel.jetbrainsindexmcpplugin.util

import com.github.hechtcarmel.jetbrainsindexmcpplugin.McpConstants
import com.github.hechtcarmel.jetbrainsindexmcpplugin.server.McpServerService
import com.github.hechtcarmel.jetbrainsindexmcpplugin.settings.McpSettings


/**
 * Generates CLI installation and usage instructions.
 *
 * This utility provides instructions for:
 * - Installing the jetbrains-cli npm package
 * - Connecting to the IDE server
 * - Using the CLI with AI coding assistants
 */
object ClientConfigGenerator {

    /**
     * Gets the server URL, using the running server URL if available,
     * or constructing a URL from settings if the server is not running.
     */
    private fun getServerUrlOrDefault(): String {
        return McpServerService.getInstance().getServerUrl()
            ?: run {
                val settings = McpSettings.getInstance()
                "http://${settings.serverHost}:${settings.serverPort}/api"
            }
    }

    /**
     * Returns the IDE-specific server name (e.g., "intellij-index", "pycharm-index").
     */
    fun getDefaultServerName(): String = McpConstants.getServerName()

    /**
     * Generates CLI installation instructions.
     */
    fun generateInstallInstructions(): String {
        return """
# JetBrains CLI Installation

## Install the CLI

```bash
npm install -g jetbrains-cli
```

## Usage

The CLI connects to your JetBrains IDE (IntelliJ, PyCharm, WebStorm, GoLand, PhpStorm, RustRover)
through a local HTTP server.

### Prerequisites
1. Open your project in a JetBrains IDE
2. Ensure the "Index MCP Server" plugin is installed and running
3. The server runs on port ${McpSettings.getInstance().serverPort} by default

### Example Commands

```bash
# Find usages of a symbol
jetbrains-cli find-usages --file src/Foo.kt --line 10 --column 5

# Find definition
jetbrains-cli find-definition --file src/Foo.kt --line 10 --column 5

# Find class
jetbrains-cli find-class --query MyService

# Get diagnostics
jetbrains-cli diagnostics --file src/Foo.kt

# List all available tools
jetbrains-cli list-tools
```

### Server URL

The server is running at: ${getServerUrlOrDefault()}

For more information, see the README at:
https://github.com/hechtcarmel/jetbrains-index-mcp-plugin
        """.trimIndent()
    }

    /**
     * Generates Claude Code skill configuration.
     * This tells Claude Code how to use the CLI.
     */
    fun generateClaudeCodeSkillConfig(): String {
        val serverName = getDefaultServerName()
        return """
# Add to Claude Code skill file

The jetbrains-cli provides semantic code intelligence for JetBrains IDEs.

## When to use
- Finding references/usages instead of grep
- Going to definitions
- Finding implementations
- Getting diagnostics/errors
- Refactoring (rename, move, delete)

## Example usage
```bash
jetbrains-cli find-usages --file path/to/File.kt --line 10 --column 5
jetbrains-cli find-definition --file path/to/File.kt --line 10 --column 5
jetbrains-cli diagnostics --file path/to/File.kt
```

## Server: $serverName
Running at: ${getServerUrlOrDefault()}
        """.trimIndent()
    }

    /**
     * Returns the server URL for CLI connections.
     */
    fun getServerUrl(): String = getServerUrlOrDefault()
}