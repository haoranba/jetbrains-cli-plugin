# Changelog

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
