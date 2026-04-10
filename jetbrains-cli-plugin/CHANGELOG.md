# Changelog

## [Unreleased]

## [4.11.1]

### Changed

- Renamed plugin from "JetBrains CLI Plugin" to "JetBrains CLI" (JetBrains Marketplace requirement)

## [4.11.0]

### Added

- **Debugger Tools Integration** - 22 new debugger tools for AI-assisted debugging:
  - Session Management: list configurations, start/stop sessions, get session status
  - Breakpoint Management: set, list, and remove breakpoints
  - Execution Control: resume, pause, step over/into/out, run to line, wait for pause
  - Stack & Threads: get call stack, list threads, select stack frames
  - Variable Inspection: get/set variables, evaluate expressions, view source context
- **CLI Debug Command Group** - 22 new CLI commands under `debug` subcommand
- Extended `AbstractMcpTool` with debugger helper methods
- Added debugger support detection via `DebuggerSupport` utility

### Changed

- Updated plugin ID to `com.github.haoranba.jetbrainsclipmcpplugin`
- Updated vendor information

## [4.10.1]

### Changed

- Simplified server architecture: removed MCP protocol support (SSE, Streamable HTTP)
- Server now provides simple JSON-RPC 2.0 API for CLI access
- Endpoints: `POST /api`, `GET /api/tools`, `GET /api/health`
- Updated `ClientConfigGenerator` to provide CLI installation instructions

### Removed

- MCP SSE transport (`KtorSseSessionManager`)
- MCP Streamable HTTP transport (`StreamableHttpSessionManager`)
- `initialize` and `notifications/initialized` JSON-RPC methods
- MCP-specific constants and configuration generation

[Unreleased]: https://github.com/haoranba/jetbrains-cli-plugin/compare/v4.11.0...HEAD
[4.11.0]: https://github.com/haoranba/jetbrains-cli-plugin/compare/v4.10.1...v4.11.0
[4.10.1]: https://github.com/haoranba/jetbrains-cli-plugin/commits/v4.10.1
