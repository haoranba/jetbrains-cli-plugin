# JetBrains CLI

Give AI coding assistants semantic code intelligence from JetBrains IDEs — find references, navigate definitions, refactor safely, and more.

## Overview

This project has two parts:

```
jetbrains-cli-plugin/   ← JetBrains IDE plugin (Kotlin)
cli/                    ← Command-line client (Node.js / TypeScript)
```

The **plugin** runs inside your JetBrains IDE and exposes a JSON-RPC 2.0 HTTP API. The **CLI** is a thin client you (or an AI assistant) use to call that API from the terminal.

## Quick Start

### 1. Install the Plugin

Install **JetBrains CLI** from the IDE marketplace:

`Settings` → `Plugins` → `Marketplace` → search **JetBrains CLI**

Or install manually from [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/29174-jetbrains-cli-plugin).

Open a project — the server starts automatically on a port based on your IDE:

| IDE | Default Port |
|-----|-------------|
| IntelliJ IDEA | 29170 |
| Android Studio | 29171 |
| PyCharm | 29172 |
| WebStorm | 29173 |
| GoLand | 29174 |
| PhpStorm | 29175 |

### 2. Install the CLI

```bash
npm install -g jetbrains-cli
```

Or build from source:

```bash
cd cli
npm install && npm run build && npm link
```

### 3. Use It

```bash
# Check plugin is running
curl http://127.0.0.1:29170/api/health

# Find a class
jetbrains-cli find-class --query UserService

# Find usages
jetbrains-cli find-usages --file src/Foo.kt --line 10 --column 5

# Rename symbol
jetbrains-cli rename --file src/Foo.kt --line 10 --column 5 --new-name Bar
```

## Available Commands

### Navigation
| Command | Description |
|---------|-------------|
| `find-class` | Search classes/interfaces by name (supports camelCase) |
| `find-file` | Search files by name |
| `find-symbol` | Search symbols (classes, methods, fields) by name |
| `find-usages` | Find all references to a symbol |
| `find-definition` | Go to symbol definition |
| `find-implementations` | Find implementations of an interface or abstract method |
| `find-super-methods` | Find parent methods in the hierarchy |
| `type-hierarchy` | Explore class inheritance chain |
| `call-hierarchy` | Trace method callers or callees |
| `file-structure` | View file structure (like IDE Structure view) |

### Refactoring
| Command | Description |
|---------|-------------|
| `rename` | Rename symbol and update all references |
| `move-file` | Move file and update all imports/references |
| `safe-delete` | Delete with usage checking (Java/Kotlin) |
| `reformat-code` | Reformat using project code style |
| `optimize-imports` | Optimize imports |
| `convert-java-to-kotlin` | Convert Java to Kotlin |

### Analysis
| Command | Description |
|---------|-------------|
| `diagnostics` | Get errors and warnings for a file |
| `build-project` | Trigger IDE build |
| `index-status` | Check if IDE indexing is complete |
| `sync-files` | Sync VFS/PSI cache after external file changes |

### Editor
| Command | Description |
|---------|-------------|
| `read-file` | Read file content, including library sources from JARs |
| `open-file` | Open file in editor |
| `get-active-file` | Get currently active editor file |

## Global Options

```
jetbrains-cli [options] <command>

--host <host>          Server host (default: 127.0.0.1)
--port <port>          Server port (default: 29170)
--project-path <path>  Required when multiple projects are open
--json                 Output raw JSON
```

**Multiple projects open?** Specify `--project-path`:

```bash
jetbrains-cli --project-path /Users/dev/myproject find-class --query UserService
```

## API

The plugin exposes a simple JSON-RPC 2.0 API:

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api` | POST | JSON-RPC 2.0 request |
| `/api/tools` | GET | List available tools |
| `/api/health` | GET | Health check |

```bash
# Health check
curl http://127.0.0.1:29170/api/health
# {"status":"ok","version":"4.0.0","port":29170}

# Call a tool
curl -X POST http://127.0.0.1:29170/api \
  -H "Content-Type: application/json" \
  -d '{"jsonrpc":"2.0","id":1,"method":"tools/call","params":{"name":"ide_find_class","arguments":{"query":"UserService"}}}'
```

## Supported IDEs & Languages

**Tested:** IntelliJ IDEA, Android Studio, PyCharm, WebStorm, GoLand, RustRover, PhpStorm

**Languages:** Java, Kotlin, Python, JavaScript/TypeScript, Go, PHP, Rust

## Requirements

- JetBrains IDE 2025.1+
- Node.js 18+ (for CLI)

## Documentation

- [`cli/README.md`](cli/README.md) — CLI usage and command reference
- [`jetbrains-cli-plugin/USAGE.md`](jetbrains-cli-plugin/USAGE.md) — Full tool parameter reference
- [`jetbrains-cli-plugin/README.md`](jetbrains-cli-plugin/README.md) — Plugin documentation

## License

- CLI: [MIT](cli/LICENSE)
- Plugin: [Apache 2.0](jetbrains-cli-plugin/LICENSE)
