# jetbrains-cli

CLI tool for JetBrains IDE code intelligence. Provides semantic code navigation, refactoring, and analysis capabilities through the Index MCP Server plugin.

## Prerequisites

1. **JetBrains IDE** - IntelliJ IDEA, PyCharm, WebStorm, GoLand, etc.
2. **Index MCP Server Plugin** - Must be installed and running in the IDE
3. **Node.js** - Version 18 or later

## Installation

```bash
# Install dependencies
npm install

# Build
npm run build

# Link globally (optional)
npm link
```

## Usage

```bash
# List all available tools
jetbrains-cli list-tools

# Find symbol usages
jetbrains-cli find-usages --file src/Foo.kt --line 10 --column 5

# Find symbol definition
jetbrains-cli find-definition --file src/Foo.kt --line 10 --column 5

# Search for class by name
jetbrains-cli find-class --query UserService

# Rename symbol
jetbrains-cli rename --file src/Foo.kt --line 10 --column 5 --new-name Bar

# Get diagnostics
jetbrains-cli diagnostics --file src/Foo.kt

# Build project
jetbrains-cli build-project
```

## Global Options

| Option | Description | Default |
|--------|-------------|---------|
| `--host` | Server host | `127.0.0.1` |
| `--port` | Server port | `29170` |
| `--json` | Output raw JSON | `false` |

## Commands

### Navigation

- `find-usages` - Find all references to a symbol
- `find-definition` - Find symbol definition
- `find-class` - Search classes by name
- `find-file` - Search files by name
- `find-symbol` - Search symbols by name
- `find-implementations` - Find implementations
- `find-super-methods` - Find parent methods
- `type-hierarchy` - Get type hierarchy
- `call-hierarchy` - Get call hierarchy
- `file-structure` - Get file structure

### Refactoring

- `rename` - Rename symbol or file
- `move-file` - Move file to new directory
- `safe-delete` - Safely delete element (Java/Kotlin)
- `reformat-code` - Reformat code
- `optimize-imports` - Optimize imports
- `convert-java-to-kotlin` - Convert Java to Kotlin

### Analysis

- `diagnostics` - Get code diagnostics
- `index-status` - Check IDE indexing status
- `sync-files` - Sync VFS and PSI cache
- `build-project` - Build project

### Editor

- `read-file` - Read file content
- `open-file` - Open file in editor
- `get-active-file` - Get active editor file

## Output Format

### Human-readable (default)
```
Found 3 references to Foo

src/main/Foo.kt:10:5   class Foo { ... }
src/test/FooTest.kt:3:8   import com.example.Foo
```

### JSON
```bash
jetbrains-cli find-usages --file src/Foo.kt --line 10 --column 5 --json
```

## Error Handling

- Exit code 0: Success
- Exit code 1: Error (server unavailable, tool failure, etc.)

## License

MIT