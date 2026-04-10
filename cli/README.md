# jetbrains-cli

CLI tool for JetBrains IDE code intelligence. Provides semantic code navigation, refactoring, and analysis capabilities through the JetBrains CLI Plugin.

## Prerequisites

1. **JetBrains IDE** - IntelliJ IDEA, PyCharm, WebStorm, GoLand, etc.
2. **JetBrains CLI Plugin** - Must be installed and running in the IDE
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

# Search for class by name (project sources only)
jetbrains-cli find-class --query UserService

# Search for class by name (include library dependencies)
jetbrains-cli find-class --query EnableMist --include-libraries

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
| `--project-path` | Project path (required when multiple projects are open) | - |
| `--json` | Output raw JSON | `false` |

### Multi-Project Support

When multiple projects are open in the IDE, you must specify `--project-path`:

```bash
# Specify project path
jetbrains-cli --project-path /Users/dev/myproject find-class --query UserService

# With JSON output
jetbrains-cli --project-path /Users/dev/myproject --json find-class --query UserService
```

If `--project-path` is omitted and multiple projects are open, an error is returned with available project paths.

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

## Command Details

### find-class

Search for classes/interfaces by name.

```bash
# Basic search (project sources only)
jetbrains-cli find-class --query UserService

# Include library dependencies
jetbrains-cli find-class --query EnableMongo --include-libraries

# Match modes
jetbrains-cli find-class --query USvc                    # camelCase matching
jetbrains-cli find-class --query "User*" --match-mode prefix  # prefix matching
jetbrains-cli find-class --query UserService --match-mode exact  # exact matching
```

**Options:**
| Option | Description | Default |
|--------|-------------|---------|
| `--query` | Class name to search for (required) | - |
| `--match-mode` | Match mode: substring, prefix, exact | substring |
| `--max-results` | Maximum results to return | 100 |
| `--include-libraries` | Include library classes in search | false |

### find-symbol

Search for symbols (classes, methods, fields) by name.

```bash
# Basic search
jetbrains-cli find-symbol --query findUser

# Include library dependencies
jetbrains-cli find-symbol --query println --include-libraries
```

### find-file

Search for files by name.

```bash
# Basic search
jetbrains-cli find-file --query UserService

# Include library files
jetbrains-cli find-file --query application.yml --include-libraries
```

### read-file

Read file content, including library sources from JARs.

```bash
# Read project file
jetbrains-cli read-file --file src/main/java/MyClass.java

# Read library source (use jar:// URL from find-class results)
jetbrains-cli read-file --file "jar:///path/to/lib.jar!/com/example/MyClass.class"
```

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

### Common Errors

**Multiple projects open:**
```json
{
  "error": "multiple_projects_open",
  "message": "Multiple projects are open. Please specify 'project_path' parameter.",
  "available_projects": [
    {"name": "myproject", "path": "/Users/dev/myproject"},
    {"name": "otherproject", "path": "/Users/dev/otherproject"}
  ]
}
```

**Server not running:**
```
Error: Cannot connect to JetBrains server at 127.0.0.1:29170. Is the plugin running?
```

## Server Endpoints

The plugin exposes a simple JSON-RPC 2.0 API:

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api` | POST | JSON-RPC 2.0 request handling |
| `/api/tools` | GET | List all available tools |
| `/api/health` | GET | Health check |

## License

MIT