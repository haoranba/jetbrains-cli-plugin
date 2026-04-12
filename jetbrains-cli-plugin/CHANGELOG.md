# Changelog

## [Unreleased]

### Changed
- Updated SkillInstaller to use jetbrains-cli skill
- Updated InstallSkillAction UI text for jetbrains-cli skill
- Rewrote jetbrains-cli SKILL.md with decision table and debug workflow

### Fixed
- Fixed call-hierarchy --direction and type-hierarchy --symbol flags in SKILL.md and CLI

### Removed
- Removed deprecated ide-index-mcp skill resources

## [1.0.0]

### Overview

IDE MCP is a Model Context Protocol (MCP) server plugin that exposes your IDE's powerful code intelligence to AI coding assistants like Claude Code, Cursor, Windsurf, and more.

### Universal Tools (All Supported IDEs)

These tools work in every JetBrains IDE:

- **Find References** - Locate all usages of any symbol across your project
- **Go to Definition** - Jump to symbol declarations instantly
- **Find Class** - Fast class/interface search by name with camelCase/substring/wildcard matching
- **Find File** - Fast file search by name using IDE's file index
- **Search Text** - Text search using IDE's pre-built word index with context filtering
- **Code Diagnostics** - Access errors, warnings, and available quick fixes
- **Index Status** - Check if the IDE is ready for code intelligence
- **Sync Files** - Force-sync IDE state with external file changes on demand

### Extended Tools (Language-Aware)

These tools activate based on available language plugins:

- **Type Hierarchy** - Explore class inheritance chains (Java, Kotlin, Python, JS/TS, Go, PHP, Rust)
- **Call Hierarchy** - Trace method/function call relationships (Java, Kotlin, Python, JS/TS, Go, PHP, Rust)
- **Find Implementations** - Discover all implementations of interfaces and abstract methods (Java, Kotlin, Python, JS/TS, PHP, Rust)
- **Find Super Methods** - Navigate through method override hierarchies (Java, Kotlin, Python, JS/TS, PHP)
- **File Structure** - View hierarchical file structure like IDE's Structure view (Java, Kotlin, Python, JS/TS)

### Refactoring Tools

- **Rename Refactoring** - Safe symbol renaming with automatic reference updates - works across ALL languages, fully headless
- **Reformat Code** - Reformat using project code style with import optimization
- **Optimize Imports** - Remove unused imports and organize remaining imports without reformatting
- **Safe Delete** - Remove code safely with usage checking (Java/Kotlin only)

### Debugger Tools

Comprehensive debugging support for AI-assisted debugging workflows:

- **Session Management** - List configurations, start/stop debug sessions, get session status
- **Breakpoint Management** - Set, list, and remove breakpoints
- **Execution Control** - Resume, pause, step over/into/out, run to line, wait for pause
- **Stack & Threads** - Get call stack, list threads, select stack frames
- **Variable Inspection** - Get/set variables, evaluate expressions, view source context

### Multi-Language Support

- **Java & Kotlin** - IntelliJ IDEA, Android Studio
- **Python** - PyCharm (all editions), IntelliJ IDEA with Python plugin
- **JavaScript & TypeScript** - WebStorm, IntelliJ IDEA Ultimate, PhpStorm
- **Go** - GoLand, IntelliJ IDEA Ultimate with Go plugin
- **PHP** - PhpStorm, IntelliJ IDEA Ultimate with PHP plugin
- **Rust** - RustRover, IntelliJ IDEA Ultimate with Rust plugin, CLion

### Supported AI Assistants

- Claude Code (CLI)
- Claude Desktop
- Cursor
- Windsurf
- VS Code with MCP extension
- Any MCP-compatible client