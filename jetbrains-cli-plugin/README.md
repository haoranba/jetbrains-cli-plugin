# JetBrains CLI Plugin

![Build](https://github.com/hechtcarmel/jetbrains-cli-plugin/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/29174.svg)](https://plugins.jetbrains.com/plugin/29174-jetbrains-cli-plugin)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/29174.svg)](https://plugins.jetbrains.com/plugin/29174-jetbrains-cli-plugin)

A JetBrains IDE plugin that exposes a **JSON-RPC API** for CLI tools, enabling AI coding assistants and command-line tools to leverage the IDE's powerful indexing and refactoring capabilities.

**Fully tested**: IntelliJ IDEA, PyCharm, WebStorm, GoLand, RustRover, Android Studio, PhpStorm
**May work** (untested): RubyMine, CLion, DataGrip

[!["Buy Me A Coffee"](https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png)](https://www.buymeacoffee.com/hechtcarmel)

<!-- Plugin description -->
**JetBrains CLI Plugin** provides CLI tools and AI coding assistants with access to the IDE's powerful code intelligence features through a simple JSON-RPC API.

### Features

**Multi-Language Support**
Advanced tools work across multiple languages based on available plugins:
- **Java & Kotlin** - IntelliJ IDEA, Android Studio
- **Python** - PyCharm (all editions), IntelliJ with Python plugin
- **JavaScript & TypeScript** - WebStorm, IntelliJ Ultimate, PhpStorm
- **Go** - GoLand, IntelliJ IDEA Ultimate with Go plugin
- **PHP** - PhpStorm, IntelliJ Ultimate with PHP plugin
- **Rust** - RustRover, IntelliJ IDEA Ultimate with Rust plugin, CLion

**Universal Tools (All JetBrains IDEs)**
- **Find References** - Locate all usages of any symbol across the project
- **Go to Definition** - Navigate to symbol declarations
- **Code Diagnostics** - Access errors, warnings, and quick fixes
- **Index Status** - Check if code intelligence is ready
- **Sync Files** - Force sync VFS/PSI cache after external file changes
- **Build Project** - Trigger IDE build with structured error/warning output (disabled by default)
- **Find Class** - Fast class/interface search by name with camelCase matching
- **Find File** - Fast file search by name using IDE's file index
- **Search Text** - Text search using IDE's pre-built word index
- **Read File** - Read file content by path or qualified name, including library sources (disabled by default)
- **Open File** - Open a file in the editor with optional navigation (disabled by default)
- **Get Active File** - Get currently active editor file(s) with cursor position (disabled by default)

**Extended Tools (Language-Aware)**
These tools activate based on installed language plugins:
- **Type Hierarchy** - Explore class inheritance chains
- **Call Hierarchy** - Trace method/function call relationships
- **Find Implementations** - Discover interface/abstract implementations
- **Symbol Search** - Find by name with fuzzy/camelCase matching (disabled by default)
- **Find Super Methods** - Navigate method override hierarchies
- **File Structure** - View hierarchical file structure like IDE's Structure view (disabled by default)

**Refactoring Tools**
- **Rename Refactoring** - Safe renaming with automatic related element renaming (getters/setters, overriding methods) - works across ALL languages, fully headless
- **Reformat Code** - Reformat using project code style with import optimization (disabled by default)
- **Safe Delete** - Remove code with usage checking (Java/Kotlin only)
- **Java to Kotlin Conversion** - Convert Java to Kotlin using Intellij's built-in converter (Java only)

### Why Use This Plugin?

Unlike simple text-based code analysis, this plugin gives AI assistants access to:
- **True semantic understanding** through the IDE's AST and index
- **Cross-project reference resolution** that works across files and modules
- **Multi-language support** - automatically detects and uses language-specific handlers
- **Safe refactoring operations** with automatic reference updates and undo support

Perfect for AI-assisted development workflows where accuracy and safety matter.
<!-- Plugin description end -->

## Table of Contents

- [Installation](#installation)
- [Quick Start](#quick-start)
- [CLI Installation](#cli-installation)
- [Available Tools](#available-tools)
- [Multi-Project Support](#multi-project-support)
- [Tool Window](#tool-window)
- [API Reference](#api-reference)
- [Error Codes](#error-codes)
- [Requirements](#requirements)
- [Contributing](#contributing)

## Installation

### Using the IDE built-in plugin system

<kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "JetBrains CLI"</kbd> > <kbd>Install</kbd>

### Using JetBrains Marketplace

Go to [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/29174-jetbrains-cli-plugin) and install it by clicking the <kbd>Install to ...</kbd> button.

### Manual Installation

Download the [latest release](https://plugins.jetbrains.com/plugin/29174-jetbrains-cli-plugin/versions) and install it manually:
<kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

## Quick Start

1. **Install the plugin** and restart your JetBrains IDE
2. **Open a project** - the server starts automatically with IDE-specific defaults:
   - IntelliJ IDEA: port **29170**
   - PyCharm: port **29172**
   - WebStorm: port **29173**
   - Other IDEs: See [IDE-Specific Defaults](#ide-specific-defaults)
3. **Install the CLI** (see below)
4. **Use the tool window** (bottom panel: "JetBrains CLI") to monitor commands and server status
5. **Change port** (optional): Click "Change port, disable tools" in the toolbar or go to <kbd>Settings</kbd> > <kbd>Tools</kbd> > <kbd>JetBrains CLI</kbd>

## CLI Installation

### Install from npm (recommended)

```bash
npm install -g jetbrains-cli
```

### Build from source

```bash
git clone https://github.com/hechtcarmel/jetbrains-cli-plugin.git
cd jetbrains-cli-plugin/cli
npm install
npm run build
npm link  # optional, makes 'jetbrains-cli' available globally
```

### CLI Usage

```bash
# List all available tools
jetbrains-cli list-tools

# Find class (project sources only)
jetbrains-cli find-class --query UserService

# Find class (include library dependencies)
jetbrains-cli find-class --query EnableMongo --include-libraries

# Find usages
jetbrains-cli find-usages --file src/Main.java --line 10 --column 5

# Get diagnostics
jetbrains-cli diagnostics --file src/Main.java

# Check index status
jetbrains-cli index-status
```

### Multi-Project Support

When multiple projects are open, specify `--project-path`:

```bash
jetbrains-cli --project-path /Users/dev/myproject find-class --query UserService
```

## Available Tools

The plugin provides **21 tools** organized by availability. Tools marked *(disabled by default)* can be enabled in <kbd>Settings</kbd> > <kbd>Tools</kbd> > <kbd>JetBrains CLI</kbd>.

### Universal Tools

These tools work in all supported JetBrains IDEs.

| Tool | Description |
|------|-------------|
| `ide_find_references` | Find all references to a symbol across the entire project |
| `ide_find_definition` | Find the definition/declaration location of a symbol |
| `ide_find_class` | Search for classes/interfaces by name with camelCase/substring/wildcard matching |
| `ide_find_file` | Search for files by name using IDE's file index |
| `ide_search_text` | Text search using IDE's pre-built word index with context filtering |
| `ide_diagnostics` | Analyze a file for problems (errors, warnings) and available intentions |
| `ide_index_status` | Check if the IDE is in dumb mode or smart mode |
| `ide_sync_files` | Force sync IDE's virtual file system and PSI cache with external file changes |
| `ide_build_project` | Build project using IDE's build system (JPS, Gradle, Maven) with structured errors *(disabled by default)* |
| `ide_read_file` | Read file content by path or qualified name, including library/jar sources *(disabled by default)* |
| `ide_get_active_file` | Get the currently active file(s) in the editor with cursor position *(disabled by default)* |
| `ide_open_file` | Open a file in the editor with optional line/column navigation *(disabled by default)* |
| `ide_refactor_rename` | Rename a symbol and update all references across the project (all languages) |
| `ide_move_file` | Move a file to a new directory with automatic reference, import, and package updates |
| `ide_reformat_code` | Reformat code using project code style with import optimization *(disabled by default)* |

### Extended Tools (Language-Aware)

These tools activate based on available language plugins:

| Tool | Description | Languages |
|------|-------------|-----------|
| `ide_type_hierarchy` | Get the complete type hierarchy (supertypes and subtypes) | Java, Kotlin, Python, JS/TS, Go, PHP, Rust |
| `ide_call_hierarchy` | Analyze method call relationships (callers or callees) | Java, Kotlin, Python, JS/TS, Go, PHP, Rust |
| `ide_find_implementations` | Find all implementations of an interface or abstract method | Java, Kotlin, Python, JS/TS, PHP, Rust |
| `ide_find_symbol` | Search for symbols (classes, methods, fields) by name with fuzzy/camelCase matching *(disabled by default)* | Java, Kotlin, Python, JS/TS, Go, PHP, Rust |
| `ide_find_super_methods` | Find the full inheritance hierarchy of methods that a method overrides/implements | Java, Kotlin, Python, JS/TS, PHP |
| `ide_file_structure` | Get hierarchical file structure (similar to IDE's Structure view) *(disabled by default)* | Java, Kotlin, Python, JS/TS |

### Java-Specific Refactoring Tools

| Tool | Description |
|------|-------------|
| `ide_convert_java_to_kotlin` | Convert Java files to Kotlin using IntelliJ's built-in converter *(disabled by default, requires Java + Kotlin plugins)* |
| `ide_refactor_safe_delete` | Safely delete an element, checking for usages first (Java/Kotlin only) |

> **Note**: Refactoring tools modify source files. All changes support undo via <kbd>Ctrl/Cmd+Z</kbd>.

### Tool Availability by IDE

**Fully Tested:**

| IDE | Universal | Navigation | Refactoring |
|-----|-----------|------------|-------------|
| IntelliJ IDEA | ✓ 14 tools | ✓ 6 tools | ✓ rename + reformat + safe delete + Java→Kotlin |
| Android Studio | ✓ 14 tools | ✓ 6 tools | ✓ rename + reformat + safe delete + Java→Kotlin |
| PyCharm | ✓ 14 tools | ✓ 6 tools | ✓ rename + reformat |
| WebStorm | ✓ 14 tools | ✓ 6 tools | ✓ rename + reformat |
| GoLand | ✓ 14 tools | ✓ 4 tools | ✓ rename + reformat |
| RustRover | ✓ 14 tools | ✓ 4 tools | ✓ rename + reformat |
| PhpStorm | ✓ 14 tools | ✓ 5 tools | ✓ rename + reformat |

For detailed tool documentation with parameters and examples, see [USAGE.md](USAGE.md).

## Multi-Project Support

When multiple projects are open in a single IDE window, you must specify which project to use with the `project_path` parameter:

```bash
# CLI example
jetbrains-cli --project-path /Users/dev/myproject find-class --query UserService
```

If `project_path` is omitted:
- **Single project open**: That project is used automatically
- **Multiple projects open**: An error is returned with the list of available projects

### Workspace Projects

The plugin supports **workspace projects** where a single IDE window contains multiple sub-projects as modules with separate content roots.

## Tool Window

The plugin adds a "JetBrains CLI" tool window (bottom panel) that shows:

- **Server Status**: Running indicator with server URL and port
- **Project Name**: Currently active project
- **Command History**: Log of all tool calls with:
  - Timestamp
  - Tool name
  - Status (Success/Error/Pending)
  - Parameters and results (expandable)
  - Execution duration

### Tool Window Actions

| Action | Description |
|--------|-------------|
| Refresh | Refresh server status and command history |
| Copy URL | Copy the server URL to clipboard |
| Clear History | Clear the command history |
| Export History | Export history to JSON or CSV file |

## API Reference

The plugin exposes a simple JSON-RPC 2.0 API:

### Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api` | POST | JSON-RPC 2.0 request handling |
| `/api/tools` | GET | List all available tools |
| `/api/health` | GET | Health check |

### JSON-RPC Methods

| Method | Description |
|--------|-------------|
| `tools/list` | List all available tools |
| `tools/call` | Execute a tool |
| `ping` | Health check |

### Example Request

```bash
# List tools
curl http://127.0.0.1:29170/api/tools

# Call a tool
curl -X POST http://127.0.0.1:29170/api \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "id": 1,
    "method": "tools/call",
    "params": {
      "name": "ide_find_class",
      "arguments": {
        "query": "UserService",
        "includeLibraries": true
      }
    }
  }'
```

### Health Check

```bash
curl http://127.0.0.1:29170/api/health
# Response: {"status":"ok","version":"4.0.0","port":29170}
```

## Error Codes

### JSON-RPC Standard Errors

| Code | Name | Description |
|------|------|-------------|
| -32700 | Parse Error | Failed to parse JSON-RPC request |
| -32600 | Invalid Request | Invalid JSON-RPC request format |
| -32601 | Method Not Found | Unknown method name |
| -32602 | Invalid Params | Invalid or missing parameters |
| -32603 | Internal Error | Unexpected internal error |

### Custom Errors

| Code | Name | Description |
|------|------|-------------|
| -32001 | Index Not Ready | IDE is in dumb mode (indexing in progress) |
| -32002 | File Not Found | Specified file does not exist |
| -32003 | Symbol Not Found | No symbol found at the specified position |
| -32004 | Refactoring Conflict | Refactoring cannot be completed (e.g., name conflict) |

## Settings

Configure the plugin at <kbd>Settings</kbd> > <kbd>Tools</kbd> > <kbd>JetBrains CLI</kbd>:

| Setting | Default | Description |
|---------|---------|-------------|
| Server Port | IDE-specific | Server port (range: 1024-65535, auto-restart on change). See [IDE-Specific Defaults](#ide-specific-defaults) |
| Server Host | `127.0.0.1` | Listening host. Change to `0.0.0.0` for remote/WSL access |
| Max History Size | 100 | Maximum number of commands to keep in history |
| Sync External Changes | false | Sync external file changes before operations (**WARNING: significant performance impact**) |
| Disabled Tools | 7 tools | Per-tool enable/disable toggles. Some tools are disabled by default to keep the tool list focused |

### IDE-Specific Defaults

Each JetBrains IDE has a unique default port:

| IDE | Default Port |
|-----|--------------|
| IntelliJ IDEA | 29170 |
| Android Studio | 29171 |
| PyCharm | 29172 |
| WebStorm | 29173 |
| GoLand | 29174 |
| PhpStorm | 29175 |
| RubyMine | 29176 |
| CLion | 29177 |
| RustRover | 29178 |
| DataGrip | 29179 |

## Requirements

- **JetBrains IDE** 2025.1 or later (any IDE based on IntelliJ Platform)
- **JVM** 21 or later

### Supported IDEs

**Fully Tested:**
- IntelliJ IDEA (Community/Ultimate)
- Android Studio
- PyCharm (Community/Professional)
- WebStorm
- GoLand
- RustRover
- PhpStorm

**May Work (Untested):**
- RubyMine
- CLion
- DataGrip

## Contributing

Contributions are welcome! Please:

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Run tests: `./gradlew test`
5. Submit a pull request

### Development Setup

```bash
# Build the plugin
./gradlew build

# Run IDE with plugin installed
./gradlew runIde

# Run tests
./gradlew test

# Run plugin verification
./gradlew runPluginVerifier
```

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

---

Plugin based on the [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template).