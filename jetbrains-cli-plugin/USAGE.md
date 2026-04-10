# IDE JetBrains CLI - Tool Reference

This document provides detailed documentation for all tools available in the IDE JetBrains CLI plugin.

## Tool Availability by IDE

Tools are organized into categories based on IDE compatibility:

### Universal Tools (All JetBrains IDEs)

These tools work in **every** JetBrains IDE:

| Tool | Description | Default |
|------|-------------|---------|
| `ide_find_references` | Find all references to a symbol | Enabled |
| `ide_find_definition` | Find symbol definition location | Enabled |
| `ide_find_class` | Search classes/interfaces by name | Enabled |
| `ide_find_file` | Search files by name | Enabled |
| `ide_search_text` | Text search using word index | Enabled |
| `ide_diagnostics` | Analyze code for problems and intentions | Enabled |
| `ide_index_status` | Check indexing status | Enabled |
| `ide_sync_files` | Force sync VFS/PSI cache | Enabled |
| `ide_build_project` | Build project with structured errors | Disabled |
| `ide_read_file` | Read file content by path or qualified name | Disabled |
| `ide_get_active_file` | Get currently active editor file(s) | Disabled |
| `ide_open_file` | Open file in editor with navigation | Disabled |
| `ide_refactor_rename` | Rename symbol with reference updates (all languages) | Enabled |
| `ide_move_file` | Move file to new directory with reference updates (all languages) | Enabled |
| `ide_reformat_code` | Reformat code using project code style | Disabled |

### Extended Tools (Language-Aware)

These tools activate based on available language plugins:

| Tool | Description | Languages |
|------|-------------|-----------|
| `ide_type_hierarchy` | Get type inheritance hierarchy | Java, Kotlin, Python, JS/TS, Go, PHP, Rust |
| `ide_call_hierarchy` | Analyze method call relationships | Java, Kotlin, Python, JS/TS, Go, PHP, Rust |
| `ide_find_implementations` | Find interface implementations | Java, Kotlin, Python, JS/TS, PHP, Rust |
| `ide_find_symbol` | Search symbols by name *(disabled by default)* | Java, Kotlin, Python, JS/TS, Go, PHP, Rust |
| `ide_find_super_methods` | Find overridden methods | Java, Kotlin, Python, JS/TS, PHP |
| `ide_file_structure` | Hierarchical file structure *(disabled by default)* | Java, Kotlin, Python, JS/TS |

### Java-Specific Refactoring Tools

| Tool | Description |
|------|-------------|
| `ide_convert_java_to_kotlin` | Convert Java files to Kotlin using the IDE converter *(disabled by default)* |
| `ide_refactor_safe_delete` | Safely delete with usage check |

---

## Table of Contents

- [Common Parameters](#common-parameters)
- [Universal Tools](#universal-tools)
  - [ide_find_references](#ide_find_references)
  - [ide_find_definition](#ide_find_definition)
  - [ide_find_class](#ide_find_class)
  - [ide_find_file](#ide_find_file)
  - [ide_search_text](#ide_search_text)
  - [ide_diagnostics](#ide_diagnostics)
  - [ide_index_status](#ide_index_status)
  - [ide_sync_files](#ide_sync_files)
  - [ide_build_project](#ide_build_project)
  - [ide_read_file](#ide_read_file)
  - [ide_get_active_file](#ide_get_active_file)
  - [ide_open_file](#ide_open_file)
- [Refactoring Tools](#refactoring-tools)
  - [ide_refactor_rename](#ide_refactor_rename)
  - [ide_move_file](#ide_move_file)
  - [ide_reformat_code](#ide_reformat_code)
- [Extended Tools (Language-Aware)](#extended-tools-language-aware)
  - [ide_type_hierarchy](#ide_type_hierarchy)
  - [ide_call_hierarchy](#ide_call_hierarchy)
  - [ide_find_implementations](#ide_find_implementations)
  - [ide_find_symbol](#ide_find_symbol)
  - [ide_find_super_methods](#ide_find_super_methods)
  - [ide_file_structure](#ide_file_structure)
- [Java-Specific Refactoring Tools](#java-specific-refactoring-tools)
  - [ide_convert_java_to_kotlin](#ide_convert_java_to_kotlin)
  - [ide_refactor_safe_delete](#ide_refactor_safe_delete)
- [Debugger Tools](#debugger-tools)
  - [ide_list_run_configurations](#ide_list_run_configurations)
  - [ide_start_debug_session](#ide_start_debug_session)
  - [ide_stop_debug_session](#ide_stop_debug_session)
  - [ide_get_debug_session_status](#ide_get_debug_session_status)
  - [ide_list_debug_sessions](#ide_list_debug_sessions)
  - [ide_execute_run_configuration](#ide_execute_run_configuration)
  - [ide_list_breakpoints](#ide_list_breakpoints)
  - [ide_set_breakpoint](#ide_set_breakpoint)
  - [ide_remove_breakpoint](#ide_remove_breakpoint)
  - [ide_resume_execution](#ide_resume_execution)
  - [ide_pause_execution](#ide_pause_execution)
  - [ide_step_over](#ide_step_over)
  - [ide_step_into](#ide_step_into)
  - [ide_step_out](#ide_step_out)
  - [ide_run_to_line](#ide_run_to_line)
  - [ide_wait_for_pause](#ide_wait_for_pause)
  - [ide_get_stack_trace](#ide_get_stack_trace)
  - [ide_list_threads](#ide_list_threads)
  - [ide_select_stack_frame](#ide_select_stack_frame)
  - [ide_get_variables](#ide_get_variables)
  - [ide_set_variable](#ide_set_variable)
  - [ide_evaluate_expression](#ide_evaluate_expression)
  - [ide_get_source_context](#ide_get_source_context)
- [Error Handling](#error-handling)

---

## Common Parameters

All tools accept an optional `project_path` parameter:

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `project_path` | string | No | Absolute path to the project root. Required when multiple projects are open in the IDE. For workspace projects, use the sub-project path. |

### Search Scope Parameter

Many search tools accept an `includeLibraries` parameter:

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `includeLibraries` | boolean | `false` | Include results from library dependencies. When `false`, only searches project source code. When `true`, also searches classes/files/symbols from JAR dependencies. |

**Tools that support `includeLibraries`:**
- `ide_find_class` - Search classes from dependencies
- `ide_find_file` - Search files from dependencies
- `ide_find_symbol` - Search symbols from dependencies

**Example:**
```json
{
  "name": "ide_find_class",
  "arguments": {
    "query": "EnableMongo",
    "includeLibraries": true
  }
}
```

### Position Parameters

Most tools operate on a specific location in code and require these parameters:

| Parameter | Type | Description |
|-----------|------|-------------|
| `file` | string | Path to the file relative to project root (e.g., `src/main/java/MyClass.java`) |
| `line` | integer | 1-based line number |
| `column` | integer | 1-based column number |

### Symbol Reference Parameters

Some tools support identifying the target element by fully qualified symbol reference instead of file position. The following parameters are available as an alternative to `file` + `line` + `column`:

| Parameter | Type | Description |
|-----------|------|-------------|
| `language` | string | Language of the symbol (e.g., `"Java"`). Required when using `symbol`. |
| `symbol` | string | Fully qualified symbol reference. Format: `com.example.ClassName`, `com.example.ClassName#memberName`. |

**Important:** The two parameter groups are **mutually exclusive** — provide either `file` + `line` + `column` OR `language` + `symbol`, not both.

**Supported languages:** Java (more languages planned).

**Tools that support symbol references:** `ide_find_references`, `ide_find_definition`, `ide_call_hierarchy`, `ide_find_implementations`, `ide_find_super_methods`.

---

## Universal Tools

These tools work in all JetBrains IDEs (IntelliJ, PyCharm, WebStorm, GoLand, etc.).

### ide_find_references

Finds all references to a symbol across the entire project using IntelliJ's semantic index.

**Use when:**
- Locating where a method, class, variable, or field is called or accessed
- Understanding code dependencies
- Preparing for refactoring

**Target (mutually exclusive):** `file` + `line` + `column` OR `language` + `symbol`

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `file` | string | Conditional | Path to the file relative to project root. Required for position-based lookup. |
| `line` | integer | Conditional | 1-based line number. Required for position-based lookup. |
| `column` | integer | Conditional | 1-based column number. Required for position-based lookup. |
| `language` | string | Conditional | Language of the symbol (e.g., `"Java"`). Required for symbol-based lookup. |
| `symbol` | string | Conditional | Fully qualified symbol reference. Required for symbol-based lookup. |
| `maxResults` | integer | No | Maximum number of references to return (default: 100, max: 500) |

**Example Request (position-based):**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_find_references",
    "arguments": {
      "file": "src/main/java/com/example/UserService.java",
      "line": 15,
      "column": 20
    }
  }
}
```

**Example Request (symbol-based):**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_find_references",
    "arguments": {
      "language": "Java",
      "symbol": "com.example.UserService#findUser(String)"
    }
  }
}
```

**Example Response:**

```json
{
  "usages": [
    {
      "file": "src/main/java/com/example/UserController.java",
      "line": 42,
      "column": 15,
      "context": "userService.findUser(id)",
      "type": "METHOD_CALL",
      "astPath": ["UserController", "getUser"]
    },
    {
      "file": "src/test/java/com/example/UserServiceTest.java",
      "line": 28,
      "column": 10,
      "context": "service.findUser(\"test\")",
      "type": "METHOD_CALL",
      "astPath": ["UserServiceTest", "testFindUser"]
    }
  ],
  "totalCount": 2
}
```

**Reference Types:**
- `METHOD_CALL` - Method invocation
- `FIELD_ACCESS` - Field read/write
- `REFERENCE` - General reference
- `IMPORT` - Import statement
- `PARAMETER` - Method parameter
- `VARIABLE` - Variable usage

---

### ide_find_definition

Finds the definition/declaration location of a symbol at a given source location.

**Use when:**
- Understanding where a method, class, variable, or field is declared
- Looking up the original definition from a usage site

**Target (mutually exclusive):** `file` + `line` + `column` OR `language` + `symbol`

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `file` | string | Conditional | Path to the file relative to project root. Required for position-based lookup. |
| `line` | integer | Conditional | 1-based line number. Required for position-based lookup. |
| `column` | integer | Conditional | 1-based column number. Required for position-based lookup. |
| `language` | string | Conditional | Language of the symbol (e.g., `"Java"`). Required for symbol-based lookup. |
| `symbol` | string | Conditional | Fully qualified symbol reference. Required for symbol-based lookup. |
| `maxPreviewLines` | integer | No | Limit `fullElementPreview` output size (default: 50, max: 500) |

**Example Request (position-based):**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_find_definition",
    "arguments": {
      "file": "src/main/java/com/example/App.java",
      "line": 25,
      "column": 12
    }
  }
}
```

**Example Request (symbol-based):**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_find_definition",
    "arguments": {
      "language": "Java",
      "symbol": "com.example.UserService#findUser(String)"
    }
  }
}
```

**Example Response:**

```json
{
  "file": "src/main/java/com/example/UserService.java",
  "line": 15,
  "column": 17,
  "preview": "14:     \n15:     public User findUser(String id) {\n16:         return userRepository.findById(id);\n17:     }",
  "symbolName": "findUser",
  "astPath": ["UserService"]
}
```

---

### ide_find_class

Searches for classes and interfaces by name using the IDE's class index.

**Use when:**
- Finding a class by name when you don't know the file path
- Discovering all classes matching a pattern

**Matching modes:**
- Substring: `"Service"` matches `"UserService"`, `"OrderService"`
- CamelCase: `"USvc"` matches `"UserService"`
- Wildcard: `"User*Impl"` matches `"UserServiceImpl"`
- Exact: case-sensitive exact match

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `query` | string | Yes | Search pattern |
| `includeLibraries` | boolean | No | Include classes from dependencies (default: false) |
| `language` | string | No | Filter by language (e.g., `"Kotlin"`, `"Java"`, `"Python"`). Case-insensitive |
| `matchMode` | string | No | `"substring"` (default), `"prefix"`, or `"exact"` |
| `limit` | integer | No | Maximum results (default: 25, max: 100) |

**Example Request:**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_find_class",
    "arguments": {
      "query": "UserService",
      "language": "Kotlin"
    }
  }
}
```

**Example Response:**

```json
{
  "classes": [
    {
      "name": "UserService",
      "qualifiedName": "com.example.service.UserService",
      "kind": "INTERFACE",
      "file": "src/main/kotlin/com/example/service/UserService.kt",
      "line": 12,
      "column": 18
    }
  ],
  "totalCount": 1,
  "query": "UserService"
}
```

---

### ide_find_file

Searches for files by name using the IDE's file index.

**Use when:**
- Finding a file when you know part of its name
- Discovering test files, config files, etc.

**Matching:** CamelCase (`"USJ"` matches `"UserService.java"`), substring, and wildcard (`"*Test.kt"`).

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `query` | string | Yes | File name pattern |
| `includeLibraries` | boolean | No | Include files from dependencies (default: false) |
| `limit` | integer | No | Maximum results (default: 25, max: 100) |

**Example Request:**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_find_file",
    "arguments": {
      "query": "UserService"
    }
  }
}
```

**Example Response:**

```json
{
  "files": [
    {
      "name": "UserService.kt",
      "path": "src/main/kotlin/com/example/service/UserService.kt",
      "directory": "src/main/kotlin/com/example/service"
    }
  ],
  "totalCount": 1,
  "query": "UserService"
}
```

---

### ide_search_text

Searches for text using the IDE's pre-built word index. Significantly faster than file scanning.

**Use when:**
- Searching for exact word occurrences across the codebase
- Finding string literals, comments, or code patterns
- Filtering searches by context (code only, comments only, strings only)

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `query` | string | Yes | Exact word to search for (not a pattern/regex) |
| `context` | string | No | Where to search: `"code"`, `"comments"`, `"strings"`, or `"all"` (default) |
| `caseSensitive` | boolean | No | Case sensitive search (default: true) |
| `filePattern` | string | No | Glob pattern to filter files (e.g., `"*.kt"`, `"*.gradle.kts"`) |
| `limit` | integer | No | Maximum results (default: 100, max: 500) |

**Example Request:**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_search_text",
    "arguments": {
      "query": "TODO",
      "context": "comments",
      "filePattern": "*.kt"
    }
  }
}
```

**Example Response:**

```json
{
  "matches": [
    {
      "file": "src/main/kotlin/com/example/UserService.kt",
      "line": 42,
      "column": 8,
      "context": "// TODO: add caching",
      "contextType": "COMMENT"
    }
  ],
  "totalCount": 1,
  "query": "TODO"
}
```

---

### ide_diagnostics

> **Availability**: Universal Tool - works in all JetBrains IDEs

Analyzes a file for code problems (errors, warnings) and available intentions/quick fixes.

**Use when:**
- Finding code issues in a file
- Checking code quality
- Identifying potential bugs
- Discovering available code improvements

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `file` | string | Yes | Path to the file relative to project root |
| `line` | integer | No | 1-based line number for intention lookup (default: 1) |
| `column` | integer | No | 1-based column number for intention lookup (default: 1) |
| `startLine` | integer | No | Filter problems to start from this line |
| `endLine` | integer | No | Filter problems to end at this line |

**Example Request:**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_diagnostics",
    "arguments": {
      "file": "src/main/java/com/example/UserService.java"
    }
  }
}
```

**Example Response:**

```json
{
  "problems": [
    {
      "message": "Field 'logger' can be made final",
      "severity": "WARNING",
      "file": "src/main/java/com/example/UserService.java",
      "line": 8,
      "column": 12,
      "endLine": 8,
      "endColumn": 18
    },
    {
      "message": "Unused import 'java.util.Date'",
      "severity": "WARNING",
      "file": "src/main/java/com/example/UserService.java",
      "line": 3,
      "column": 1,
      "endLine": 3,
      "endColumn": 22
    }
  ],
  "intentions": [
    {
      "name": "Add 'final' modifier",
      "description": "Makes the field final"
    },
    {
      "name": "Optimize imports",
      "description": "Removes unused imports"
    }
  ],
  "problemCount": 2,
  "intentionCount": 2
}
```

**Severity Values:**
- `ERROR` - Compilation error
- `WARNING` - Potential problem
- `WEAK_WARNING` - Minor issue
- `INFO` - Informational

---

### ide_index_status

> **Availability**: Universal Tool - works in all JetBrains IDEs

Checks if the IDE is in dumb mode (indexing) or smart mode.

**Use when:**
- Checking if index-dependent operations will work
- Waiting for indexing to complete

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| (none) | | | No parameters required |

**Example Request:**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_index_status",
    "arguments": {}
  }
}
```

**Example Response:**

```json
{
  "isDumbMode": false,
  "isSmartMode": true,
  "isIndexing": false,
  "projectName": "my-application"
}
```

---

### ide_sync_files

Force the IDE to synchronize its virtual file system and PSI cache with external file changes.

**Use when:**
- Files were created, modified, or deleted outside the IDE (e.g., by coding agents)
- Other IDE tools report stale results or miss references in recently changed files

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `paths` | array of strings | No | File or directory paths relative to project root to sync. If omitted, syncs the entire project |

**Example Request:**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_sync_files",
    "arguments": {
      "paths": ["src/main/java/com/example/NewFile.java"]
    }
  }
}
```

**Example Response:**

```json
{
  "syncedPaths": ["src/main/java/com/example/NewFile.java"],
  "syncedAll": false,
  "message": "Synced 1 path(s)"
}
```

---

### ide_build_project

> **Default**: Disabled - enable in Settings > Tools > JetBrains CLI

Build the project using the IDE's build system (supports JPS, Gradle, Maven).

**Use when:**
- Checking for compilation errors after code changes
- Verifying that refactoring didn't break anything
- Getting structured error messages with file locations

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `rebuild` | boolean | No | Full rebuild instead of incremental build (default: false) |
| `includeRawOutput` | boolean | No | Include raw build output log (default: false) |
| `timeoutSeconds` | integer | No | Timeout in seconds. No timeout if omitted |

**Example Request:**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_build_project",
    "arguments": {}
  }
}
```

**Example Response:**

```json
{
  "success": false,
  "aborted": false,
  "errors": 1,
  "warnings": 2,
  "buildMessages": [
    {
      "severity": "ERROR",
      "message": "Unresolved reference: fooBar",
      "file": "src/main/kotlin/com/example/App.kt",
      "line": 15,
      "column": 10
    }
  ],
  "truncated": false,
  "durationMs": 3200
}
```

---

### ide_read_file

> **Default**: Disabled - enable in Settings > Tools > JetBrains CLI

Read file content by file path or fully qualified class name.

**Use when:**
- Reading library/dependency source code from JARs
- Looking up class source by qualified name (e.g., `java.util.ArrayList`)
- Reading project files with metadata

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `file` | string | No* | File path (relative, absolute, or jar path with `!/` or `jar://`) |
| `qualifiedName` | string | No* | Fully qualified class name (e.g., `java.util.ArrayList`) |
| `startLine` | integer | No | Starting line (1-based, inclusive) |
| `endLine` | integer | No | Ending line (1-based, inclusive) |

*Either `file` or `qualifiedName` must be provided.

**Example Request:**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_read_file",
    "arguments": {
      "qualifiedName": "java.util.ArrayList",
      "startLine": 1,
      "endLine": 50
    }
  }
}
```

**Example Response:**

```json
{
  "file": "jar:///path/to/jdk/src.zip!/java.base/java/util/ArrayList.java",
  "content": "...",
  "language": "JAVA",
  "lineCount": 1750,
  "startLine": 1,
  "endLine": 50,
  "isLibraryFile": true
}
```

---

### ide_get_active_file

> **Default**: Disabled - enable in Settings > Tools > JetBrains CLI

Get the currently active file(s) open in the IDE editor, including split panes.

**Use when:**
- Understanding what the user is currently looking at
- Getting cursor position and selected text

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| (none) | | | Only `project_path` if multiple projects are open |

**Example Request:**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_get_active_file",
    "arguments": {}
  }
}
```

**Example Response:**

```json
{
  "activeFiles": [
    {
      "file": "src/main/kotlin/com/example/UserService.kt",
      "line": 25,
      "column": 10,
      "selectedText": null,
      "hasSelection": false,
      "language": "Kotlin"
    }
  ]
}
```

---

### ide_open_file

> **Default**: Disabled - enable in Settings > Tools > JetBrains CLI

Open a file in the IDE editor with optional line/column navigation.

**Use when:**
- Directing the user's attention to a specific file and location
- Opening a file after finding it via search

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `file` | string | Yes | File path relative to project root, or absolute path |
| `line` | integer | No | 1-based line number to navigate to |
| `column` | integer | No | 1-based column number (requires `line`) |

**Example Request:**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_open_file",
    "arguments": {
      "file": "src/main/kotlin/com/example/UserService.kt",
      "line": 25
    }
  }
}
```

**Example Response:**

```json
{
  "file": "src/main/kotlin/com/example/UserService.kt",
  "opened": true,
  "message": "Opened file at line 25"
}
```

---

## Refactoring Tools

> **Note**: All refactoring tools modify source files. Changes can be undone with Ctrl/Cmd+Z.

### ide_refactor_rename (Universal - All Languages)

Renames a symbol and updates all references across the project. This tool uses IntelliJ's `RenameProcessor` which is language-agnostic and works across **all languages** supported by your IDE.

**Supported Languages:** Java, Kotlin, Python, JavaScript, TypeScript, Go, PHP, Rust, Ruby, and any language with IntelliJ plugin support.

**Features:**
- Language-specific name validation (identifier rules, keyword detection)
- **Fully headless/autonomous operation** (no popups or dialogs)
- **Automatic related element renaming** - getters/setters, overriding methods, test classes are renamed automatically
- Conflict detection before rename execution (returns error instead of showing dialog)
- Single atomic operation - all renames (primary + related) can be undone with one Ctrl/Cmd+Z

**Use when:**
- Renaming identifiers to improve code clarity
- Following naming conventions
- Refactoring code structure

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `file` | string | Yes | Path to the file containing the symbol |
| `line` | integer | Yes | 1-based line number |
| `column` | integer | Yes | 1-based column number |
| `newName` | string | Yes | The new name for the symbol |
| `overrideStrategy` | string | No | How to handle overriding methods: `"rename_base"` (default), `"rename_only_current"`, or `"ask"` |
| `relatedRenamingStrategy` | string | No | How to handle automatic renaming of related symbols: `"all"` (default), `"none"`, `"accessors_and_tests"`, or `"ask"` |

**Example Request (Java):**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_refactor_rename",
    "arguments": {
      "file": "src/main/java/com/example/UserService.java",
      "line": 15,
      "column": 17,
      "newName": "findUserById"
    }
  }
}
```

**Example Request (Python):**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_refactor_rename",
    "arguments": {
      "file": "src/services/user_service.py",
      "line": 10,
      "column": 5,
      "newName": "fetch_user_data"
    }
  }
}
```

**Example Request (PHP):**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_refactor_rename",
    "arguments": {
      "file": "src/Models/User.php",
      "line": 25,
      "column": 21,
      "newName": "getFullName"
    }
  }
}
```

**Example Response:**

```json
{
  "success": true,
  "affectedFiles": [
    "src/main/java/com/example/UserService.java",
    "src/main/java/com/example/UserController.java",
    "src/test/java/com/example/UserServiceTest.java"
  ],
  "changesCount": 3,
  "message": "Successfully renamed 'findUser' to 'findUserById' (also renamed 2 related element(s))"
}
```

**Automatic Related Renames:**

Related elements are automatically renamed without any prompts or dialogs:

| Language | What Gets Auto-Renamed |
|----------|------------------------|
| Java/Kotlin | Getters/setters for fields, constructor parameters matching fields, overriding methods in subclasses, test classes |
| All Languages | Method implementations in subclasses, interface method implementations |

All renames happen in a single atomic operation, so one undo (Ctrl/Cmd+Z) reverts everything.

---

### ide_move_file

Move a file to a new directory using the IDE's refactoring engine. Automatically updates all references, imports, and package declarations across the project.

**Supported Languages:** Java, Kotlin, Python, JavaScript, TypeScript, Go, PHP, Rust, and any language with IntelliJ plugin support.

**Features:**
- Updates all imports and references across the entire project
- Updates package declarations (Java/Kotlin)
- Automatically creates destination directory if it doesn't exist
- Detects name conflicts at the destination
- Optional reference search toggle for non-code files

**Use when:**
- Reorganizing project structure
- Moving classes to different packages
- Relocating files while maintaining correct imports

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `file` | string | Yes | Path to the source file to move, relative to project root |
| `destination` | string | Yes | Target directory path relative to project root |
| `update_references` | boolean | No | Whether to update references (default: `true`) |

**Example Request:**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_move_file",
    "arguments": {
      "file": "src/main/java/com/old/MyService.java",
      "destination": "src/main/java/com/new/services"
    }
  }
}
```

**Example Request (skip reference updates):**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_move_file",
    "arguments": {
      "file": "config/old-config.yml",
      "destination": "config/archive",
      "update_references": false
    }
  }
}
```

**Example Response:**

```json
{
  "success": true,
  "affectedFiles": [
    "src/main/java/com/old/MyService.java",
    "src/main/java/com/new/services/MyService.java"
  ],
  "changesCount": 2,
  "message": "Successfully moved 'src/main/java/com/old/MyService.java' to 'src/main/java/com/new/services/MyService.java' (references updated)"
}
```

---

### ide_reformat_code

> **Default**: Disabled - enable in Settings > Tools > JetBrains CLI

Reformat code according to the project's code style settings. Equivalent to the IDE's "Reformat Code" action (<kbd>Ctrl+Alt+L</kbd> / <kbd>Cmd+Opt+L</kbd>).

**Use when:**
- Applying consistent formatting after code changes
- Organizing imports
- Rearranging code members according to project rules

**Respects:** `.editorconfig`, project code style, language-specific formatting rules.

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `file` | string | Yes | File path relative to project root |
| `startLine` | integer | No | Start line for partial formatting (1-based). Requires `endLine` |
| `endLine` | integer | No | End line for partial formatting (1-based). Requires `startLine` |
| `optimizeImports` | boolean | No | Optimize imports (default: true) |
| `rearrangeCode` | boolean | No | Rearrange code members (default: true) |

**Example Request:**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_reformat_code",
    "arguments": {
      "file": "src/main/kotlin/com/example/UserService.kt"
    }
  }
}
```

**Example Response:**

```json
{
  "success": true,
  "affectedFiles": ["src/main/kotlin/com/example/UserService.kt"],
  "changesCount": 1,
  "message": "Reformatted code (optimized imports, rearranged code)"
}
```

---

## Extended Tools (Language-Aware)

These tools activate based on available language plugins:
- **Java/Kotlin** - IntelliJ IDEA, Android Studio
- **Python** - PyCharm (all editions), IntelliJ with Python plugin
- **JavaScript/TypeScript** - WebStorm, IntelliJ Ultimate, PhpStorm
- **Go** - GoLand, IntelliJ Ultimate with Go plugin
- **PHP** - PhpStorm, IntelliJ Ultimate with PHP plugin
- **Rust** - RustRover, IntelliJ Ultimate with Rust plugin, CLion

In IDEs without language-specific plugins (e.g., DataGrip), these tools will not appear in the tools list.

### ide_type_hierarchy

Retrieves the complete type hierarchy for a class or interface.

**Use when:**
- Exploring class inheritance chains
- Understanding polymorphism
- Finding all subclasses or implementations
- Analyzing interface implementations

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `file` | string | No* | Path to the file relative to project root |
| `line` | integer | No* | 1-based line number |
| `column` | integer | No* | 1-based column number |
| `className` | string | No* | Fully qualified class name (alternative to position) |

*Either `file`/`line`/`column` OR `className` must be provided.

**Example Request (by position):**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_type_hierarchy",
    "arguments": {
      "file": "src/main/java/com/example/ArrayList.java",
      "line": 5,
      "column": 14
    }
  }
}
```

**Example Request (by class name - Java):**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_type_hierarchy",
    "arguments": {
      "className": "java.util.ArrayList"
    }
  }
}
```

**Example Request (by class name - PHP):**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_type_hierarchy",
    "arguments": {
      "className": "App\\Models\\User"
    }
  }
}
```

**Example Response:**

```json
{
  "element": {
    "name": "com.example.UserServiceImpl",
    "file": "src/main/java/com/example/UserServiceImpl.java",
    "kind": "CLASS"
  },
  "supertypes": [
    {
      "name": "com.example.UserService",
      "file": "src/main/java/com/example/UserService.java",
      "kind": "INTERFACE"
    },
    {
      "name": "com.example.BaseService",
      "file": "src/main/java/com/example/BaseService.java",
      "kind": "ABSTRACT_CLASS"
    }
  ],
  "subtypes": [
    {
      "name": "com.example.AdminUserServiceImpl",
      "file": "src/main/java/com/example/AdminUserServiceImpl.java",
      "kind": "CLASS"
    }
  ]
}
```

**Kind Values:**
- `CLASS` - Concrete class
- `ABSTRACT_CLASS` - Abstract class
- `INTERFACE` - Interface
- `ENUM` - Enum type
- `ANNOTATION` - Annotation type
- `RECORD` - Record class (Java 16+)

---

### ide_call_hierarchy

Analyzes method call relationships to find callers or callees.

**Use when:**
- Tracing execution flow
- Understanding code dependencies
- Analyzing impact of method changes
- Debugging to understand how a method is reached

**Target (mutually exclusive):** `file` + `line` + `column` OR `language` + `symbol`

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `file` | string | Conditional | Path to the file relative to project root. Required for position-based lookup. |
| `line` | integer | Conditional | 1-based line number. Required for position-based lookup. |
| `column` | integer | Conditional | 1-based column number. Required for position-based lookup. |
| `language` | string | Conditional | Language of the symbol (e.g., `"Java"`). Required for symbol-based lookup. |
| `symbol` | string | Conditional | Fully qualified symbol reference. Required for symbol-based lookup. |
| `direction` | string | Yes | `"callers"` or `"callees"` |
| `depth` | integer | No | How deep to traverse (default: 3, max: 5) |

**Example Request (position-based):**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_call_hierarchy",
    "arguments": {
      "file": "src/main/java/com/example/UserService.java",
      "line": 20,
      "column": 10,
      "direction": "callers"
    }
  }
}
```

**Example Request (symbol-based):**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_call_hierarchy",
    "arguments": {
      "language": "Java",
      "symbol": "com.example.UserService#validateUser(String)",
      "direction": "callers"
    }
  }
}
```

**Example Response:**

```json
{
  "element": {
    "name": "UserService.validateUser(String)",
    "file": "src/main/java/com/example/UserService.java",
    "line": 20,
    "column": 17
  },
  "calls": [
    {
      "name": "UserController.createUser(UserRequest)",
      "file": "src/main/java/com/example/UserController.java",
      "line": 45,
      "column": 17
    },
    {
      "name": "UserController.updateUser(String, UserRequest)",
      "file": "src/main/java/com/example/UserController.java",
      "line": 62,
      "column": 17
    }
  ]
}
```

---

### ide_find_implementations

Finds all concrete implementations of an interface, abstract class, or abstract method.

**Languages:** Java, Kotlin, Python, JS/TS, PHP, Rust (not Go — Go uses implicit interfaces).

**Use when:**
- Locating classes that implement an interface
- Finding classes that extend an abstract class
- Finding all overriding methods for polymorphic behavior analysis

**Target (mutually exclusive):** `file` + `line` + `column` OR `language` + `symbol`

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `file` | string | Conditional | Path to the file relative to project root. Required for position-based lookup. |
| `line` | integer | Conditional | 1-based line number. Required for position-based lookup. |
| `column` | integer | Conditional | 1-based column number. Required for position-based lookup. |
| `language` | string | Conditional | Language of the symbol (e.g., `"Java"`). Required for symbol-based lookup. |
| `symbol` | string | Conditional | Fully qualified symbol reference. Required for symbol-based lookup. |

**Example Request (position-based):**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_find_implementations",
    "arguments": {
      "file": "src/main/java/com/example/Repository.java",
      "line": 8,
      "column": 10
    }
  }
}
```

**Example Request (symbol-based):**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_find_implementations",
    "arguments": {
      "language": "Java",
      "symbol": "com.example.Repository"
    }
  }
}
```

**Example Response:**

```json
{
  "implementations": [
    {
      "name": "com.example.JpaUserRepository",
      "file": "src/main/java/com/example/JpaUserRepository.java",
      "line": 12,
      "column": 14,
      "kind": "CLASS"
    },
    {
      "name": "com.example.InMemoryUserRepository",
      "file": "src/main/java/com/example/InMemoryUserRepository.java",
      "line": 8,
      "column": 14,
      "kind": "CLASS"
    }
  ],
  "totalCount": 2
}
```

---

### ide_find_symbol

> **Default**: Disabled - enable in Settings > Tools > JetBrains CLI

Searches for code symbols (classes, interfaces, methods, fields) by name using the IDE's semantic index.

**Use when:**
- Finding a class or interface by name (e.g., find "UserService")
- Locating methods across the codebase (e.g., find all "findById" methods)
- Discovering fields or constants by name
- Navigating to code when you know the symbol name but not the file location

**Supports fuzzy matching:**
- Substring: "Service" matches "UserService", "OrderService"
- CamelCase: "USvc" matches "UserService", "US" matches "UserService"

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `query` | string | Yes | Search pattern (supports substring and camelCase matching) |
| `includeLibraries` | boolean | No | Include symbols from library dependencies (default: false) |
| `language` | string | No | Filter by language (e.g., `"Kotlin"`, `"Java"`). Case-insensitive |
| `matchMode` | string | No | `"substring"` (default), `"prefix"`, or `"exact"` |
| `limit` | integer | No | Maximum results to return (default: 25, max: 100) |

**Example Request:**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_find_symbol",
    "arguments": {
      "query": "UserService"
    }
  }
}
```

**Example Request (camelCase matching):**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_find_symbol",
    "arguments": {
      "query": "USvc",
      "includeLibraries": true,
      "limit": 50
    }
  }
}
```

**Example Response:**

```json
{
  "symbols": [
    {
      "name": "UserService",
      "qualifiedName": "com.example.service.UserService",
      "kind": "INTERFACE",
      "file": "src/main/java/com/example/service/UserService.java",
      "line": 12,
      "column": 18,
      "containerName": null
    },
    {
      "name": "UserServiceImpl",
      "qualifiedName": "com.example.service.UserServiceImpl",
      "kind": "CLASS",
      "file": "src/main/java/com/example/service/UserServiceImpl.java",
      "line": 15,
      "column": 14,
      "containerName": null
    },
    {
      "name": "findUser",
      "qualifiedName": "com.example.service.UserService.findUser",
      "kind": "METHOD",
      "file": "src/main/java/com/example/service/UserService.java",
      "line": 18,
      "column": 10,
      "containerName": "UserService"
    }
  ],
  "totalCount": 3,
  "query": "UserService"
}
```

**Kind Values:**
- `CLASS` - Concrete class
- `ABSTRACT_CLASS` - Abstract class
- `INTERFACE` - Interface
- `ENUM` - Enum type
- `ANNOTATION` - Annotation type
- `RECORD` - Record class (Java 16+)
- `METHOD` - Method
- `FIELD` - Field or constant

---

### ide_find_super_methods

Finds the complete inheritance hierarchy for a method - all parent methods it overrides or implements.

**Languages:** Java, Kotlin, Python, JS/TS, PHP (not Go or Rust — they use composition/traits instead of classical inheritance).

**Use when:**
- Finding which interface method an implementation overrides
- Navigating to the original method declaration in a parent class
- Understanding the full inheritance chain for a method with @Override
- Seeing all levels of method overriding (not just immediate parent)

**Position flexibility:** The position (line/column) can be anywhere within the method - on the name, inside the body, or on the @Override annotation. The tool automatically finds the enclosing method.

**Target (mutually exclusive):** `file` + `line` + `column` OR `language` + `symbol`

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `file` | string | Conditional | Path to the file relative to project root. Required for position-based lookup. |
| `line` | integer | Conditional | 1-based line number (any line within the method). Required for position-based lookup. |
| `column` | integer | Conditional | 1-based column number (any position within the method). Required for position-based lookup. |
| `language` | string | Conditional | Language of the symbol (e.g., `"Java"`). Required for symbol-based lookup. |
| `symbol` | string | Conditional | Fully qualified symbol reference. Required for symbol-based lookup. |

**Example Request (position-based):**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_find_super_methods",
    "arguments": {
      "file": "src/main/java/com/example/UserServiceImpl.java",
      "line": 25,
      "column": 10
    }
  }
}
```

**Example Request (symbol-based):**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_find_super_methods",
    "arguments": {
      "language": "Java",
      "symbol": "com.example.UserServiceImpl#findUser(String)"
    }
  }
}
```

**Example Response:**

```json
{
  "method": {
    "name": "findUser",
    "signature": "findUser(String id): User",
    "containingClass": "com.example.UserServiceImpl",
    "file": "src/main/java/com/example/UserServiceImpl.java",
    "line": 25,
    "column": 17
  },
  "hierarchy": [
    {
      "name": "findUser",
      "signature": "findUser(String id): User",
      "containingClass": "com.example.AbstractUserService",
      "containingClassKind": "ABSTRACT_CLASS",
      "file": "src/main/java/com/example/AbstractUserService.java",
      "line": 18,
      "column": 17,
      "isInterface": false,
      "depth": 1
    },
    {
      "name": "findUser",
      "signature": "findUser(String id): User",
      "containingClass": "com.example.UserService",
      "containingClassKind": "INTERFACE",
      "file": "src/main/java/com/example/UserService.java",
      "line": 12,
      "column": 10,
      "isInterface": true,
      "depth": 2
    }
  ],
  "totalCount": 2
}
```

**Depth field:** The `depth` field indicates the level in the hierarchy:
- `depth: 1` = immediate parent (first level up)
- `depth: 2` = grandparent (two levels up)
- And so on...

**containingClassKind Values:**
- `CLASS` - Concrete class
- `ABSTRACT_CLASS` - Abstract class
- `INTERFACE` - Interface

---

### ide_file_structure

> **Default**: Disabled - enable in Settings > Tools > JetBrains CLI

Get the hierarchical structure of a source file, similar to the IDE's Structure view (<kbd>Cmd+7</kbd> / <kbd>Alt+7</kbd>).

**Languages:** Java, Kotlin, Python, JavaScript, TypeScript.

**Use when:**
- Getting an overview of a file's classes, methods, and fields
- Understanding code organization without reading the full file
- Navigating large files

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `file` | string | Yes | Path to the file relative to project root |

**Example Request:**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_file_structure",
    "arguments": {
      "file": "src/main/kotlin/com/example/UserService.kt"
    }
  }
}
```

**Example Response:**

```json
{
  "file": "src/main/kotlin/com/example/UserService.kt",
  "language": "Kotlin",
  "structure": "interface UserService :15\n  fun findUser(id: String): User :16\n  fun deleteUser(id: String) :17\n\nclass UserServiceImpl :20\n  val repository: UserRepository :21\n  override fun findUser(id: String): User :23\n  override fun deleteUser(id: String) :30\n  private fun validate(id: String) :37"
}
```

---

## Java-Specific Refactoring Tools

These tools require the Java plugin and are only available in **IntelliJ IDEA** and **Android Studio**.

`ide_convert_java_to_kotlin` also requires the Kotlin plugin and is disabled by default.

### ide_convert_java_to_kotlin

> **Default**: Disabled - enable in Settings > Tools > JetBrains CLI

Convert one or more Java files to Kotlin using IntelliJ's built-in J2K (Java-to-Kotlin) converter.

**Use when:**
- Migrating Java source files to Kotlin
- Converting a batch of related Java files in one request
- Letting the IDE handle syntax conversion, formatting, and import cleanup

**Features:**
- Supports batch conversion via a `files` array
- Uses the IDE's built-in converter instead of text transformation
- Automatically formats converted Kotlin files and optimizes imports
- Deletes original `.java` files after successful conversion
- Returns per-file results plus a summary of converted, skipped, and failed files

**Requirements:**
- Java plugin available
- Kotlin plugin enabled
- Files must belong to a module with Kotlin support enabled

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `files` | array of strings | Yes | Java file paths relative to project root |

**Example Request:**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_convert_java_to_kotlin",
    "arguments": {
      "files": [
        "src/main/java/com/example/User.java",
        "src/main/java/com/example/UserService.java"
      ]
    }
  }
}
```

**Example Response:**

```json
{
  "files": [
    {
      "requestedPath": "src/main/java/com/example/User.java",
      "status": "CONVERTED",
      "kotlinFile": "src/main/java/com/example/User.kt",
      "linesConverted": 42,
      "javaFileDeleted": true
    },
    {
      "requestedPath": "src/main/java/com/example/UserService.java",
      "status": "SKIPPED",
      "reason": "Module 'app' does not have Kotlin plugin enabled"
    }
  ],
  "summary": {
    "totalRequested": 2,
    "converted": 1,
    "skipped": 1,
    "failed": 0
  }
}
```

**Status Values:**
- `CONVERTED` - Successfully converted to a new `.kt` file
- `SKIPPED` - File could not be attempted (for example not found, not a Java file, or no Kotlin-enabled module)
- `FAILED` - Conversion was attempted but did not produce a Kotlin file or hit a converter error

**Notes:**
- The tool returns structured per-file results in the same order as the input list
- Duplicate paths are reported separately
- Some advanced Java constructs may still need manual cleanup after conversion

### ide_refactor_safe_delete

Safely deletes an element, first checking for usages.

**Use when:**
- Removing unused code
- Cleaning up dead code
- Safely removing methods or classes

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `file` | string | Yes | Path to the file |
| `line` | integer | Yes | 1-based line number |
| `column` | integer | Yes | 1-based column number |
| `force` | boolean | No | Force deletion even if usages exist (default: false) |

**Example Request:**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_refactor_safe_delete",
    "arguments": {
      "file": "src/main/java/com/example/LegacyHelper.java",
      "line": 8,
      "column": 14
    }
  }
}
```

**Example Response (safe to delete):**

```json
{
  "success": true,
  "message": "Successfully deleted 'LegacyHelper'"
}
```

**Example Response (blocked by usages):**

```json
{
  "success": false,
  "message": "Cannot safely delete: 3 usages found",
  "blockingUsages": [
    {
      "file": "src/main/java/com/example/App.java",
      "line": 25,
      "context": "LegacyHelper.convert(data)"
    }
  ]
}
```

---

## Debugger Tools

Comprehensive debugging support for AI-assisted debugging workflows. These tools provide full control over the IDE's debugger, including session management, breakpoints, execution control, and variable inspection.

### ide_list_run_configurations

List all available run/debug configurations in the project.

**Use when:**
- Discovering what configurations can be debugged
- Finding configuration names to start debug sessions

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| (none) | | | Only `project_path` if multiple projects are open |

**Example Request:**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_list_run_configurations",
    "arguments": {}
  }
}
```

**Example Response:**

```json
{
  "configurations": [
    {
      "name": "Application",
      "type": "Application",
      "enabled": true
    },
    {
      "name": "All Tests",
      "type": "JUnit",
      "enabled": true
    }
  ],
  "totalCount": 2
}
```

---

### ide_start_debug_session

Start a debug session for a run configuration.

**Use when:**
- Starting a debugging session
- Launching an application in debug mode

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `configurationName` | string | Yes | Name of the run configuration to debug |

**Example Request:**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_start_debug_session",
    "arguments": {
      "configurationName": "Application"
    }
  }
}
```

**Example Response:**

```json
{
  "success": true,
  "sessionId": "debug-session-1",
  "configurationName": "Application",
  "message": "Debug session started for 'Application'"
}
```

---

### ide_stop_debug_session

Stop an active debug session.

**Use when:**
- Ending a debugging session
- Cleaning up after debugging

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `sessionId` | string | No | Session ID to stop. If omitted, stops the current active session |

**Example Request:**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_stop_debug_session",
    "arguments": {}
  }
}
```

**Example Response:**

```json
{
  "success": true,
  "message": "Debug session stopped"
}
```

---

### ide_get_debug_session_status

Get the current status of a debug session.

**Use when:**
- Checking if debugger is paused or running
- Getting current execution state

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `sessionId` | string | No | Session ID to check. If omitted, checks the current active session |

**Example Request:**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_get_debug_session_status",
    "arguments": {}
  }
}
```

**Example Response:**

```json
{
  "isActive": true,
  "isPaused": true,
  "isRunning": false,
  "currentFile": "src/main/java/com/example/App.java",
  "currentLine": 42,
  "currentMethod": "main",
  "sessionId": "debug-session-1"
}
```

---

### ide_list_debug_sessions

List all active debug sessions.

**Use when:**
- Managing multiple debug sessions
- Finding session IDs

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| (none) | | | Only `project_path` if multiple projects are open |

**Example Request:**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_list_debug_sessions",
    "arguments": {}
  }
}
```

**Example Response:**

```json
{
  "sessions": [
    {
      "sessionId": "debug-session-1",
      "configurationName": "Application",
      "isActive": true,
      "isPaused": true
    }
  ],
  "totalCount": 1
}
```

---

### ide_execute_run_configuration

Execute a run configuration (run without debugging).

**Use when:**
- Running an application normally
- Executing tests without debugging

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `configurationName` | string | Yes | Name of the run configuration to execute |

**Example Request:**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_execute_run_configuration",
    "arguments": {
      "configurationName": "All Tests"
    }
  }
}
```

**Example Response:**

```json
{
  "success": true,
  "message": "Started execution of 'All Tests'"
}
```

---

### ide_list_breakpoints

List all breakpoints in the project.

**Use when:**
- Viewing all set breakpoints
- Finding breakpoint IDs for removal

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| (none) | | | Only `project_path` if multiple projects are open |

**Example Request:**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_list_breakpoints",
    "arguments": {}
  }
}
```

**Example Response:**

```json
{
  "breakpoints": [
    {
      "id": "bp-1",
      "file": "src/main/java/com/example/App.java",
      "line": 25,
      "enabled": true,
      "condition": null
    },
    {
      "id": "bp-2",
      "file": "src/main/java/com/example/Service.java",
      "line": 42,
      "enabled": true,
      "condition": "id != null"
    }
  ],
  "totalCount": 2
}
```

---

### ide_set_breakpoint

Set a breakpoint at a specific location.

**Use when:**
- Setting breakpoints for debugging
- Adding conditional breakpoints

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `file` | string | Yes | File path relative to project root |
| `line` | integer | Yes | 1-based line number |
| `condition` | string | No | Optional condition expression |
| `enabled` | boolean | No | Whether breakpoint is enabled (default: true) |

**Example Request:**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_set_breakpoint",
    "arguments": {
      "file": "src/main/java/com/example/App.java",
      "line": 42,
      "condition": "userId != null"
    }
  }
}
```

**Example Response:**

```json
{
  "success": true,
  "breakpointId": "bp-3",
  "file": "src/main/java/com/example/App.java",
  "line": 42,
  "message": "Breakpoint set at line 42"
}
```

---

### ide_remove_breakpoint

Remove a breakpoint.

**Use when:**
- Removing breakpoints after debugging
- Cleaning up breakpoints

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `breakpointId` | string | No | ID of breakpoint to remove |
| `file` | string | No | File path (alternative to breakpointId) |
| `line` | integer | No | Line number (required with file) |

**Example Request:**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_remove_breakpoint",
    "arguments": {
      "breakpointId": "bp-3"
    }
  }
}
```

**Example Response:**

```json
{
  "success": true,
  "message": "Breakpoint removed"
}
```

---

### ide_resume_execution

Resume execution of a paused debug session.

**Use when:**
- Continuing after hitting a breakpoint
- Resuming from pause

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `sessionId` | string | No | Session ID. If omitted, uses the current active session |

**Example Request:**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_resume_execution",
    "arguments": {}
  }
}
```

**Example Response:**

```json
{
  "success": true,
  "message": "Execution resumed"
}
```

---

### ide_pause_execution

Pause execution of a running debug session.

**Use when:**
- Pausing a running application
- Interrupting execution

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `sessionId` | string | No | Session ID. If omitted, uses the current active session |

**Example Request:**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_pause_execution",
    "arguments": {}
  }
}
```

**Example Response:**

```json
{
  "success": true,
  "message": "Execution paused"
}
```

---

### ide_step_over

Step over the current line (execute without entering functions).

**Use when:**
- Stepping through code line by line
- Skipping function calls

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `sessionId` | string | No | Session ID. If omitted, uses the current active session |

**Example Request:**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_step_over",
    "arguments": {}
  }
}
```

**Example Response:**

```json
{
  "success": true,
  "message": "Stepped over"
}
```

---

### ide_step_into

Step into the function at the current line.

**Use when:**
- Debugging function internals
- Following execution into called functions

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `sessionId` | string | No | Session ID. If omitted, uses the current active session |

**Example Request:**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_step_into",
    "arguments": {}
  }
}
```

**Example Response:**

```json
{
  "success": true,
  "message": "Stepped into"
}
```

---

### ide_step_out

Step out of the current function (return to caller).

**Use when:**
- Exiting the current function
- Returning to the calling context

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `sessionId` | string | No | Session ID. If omitted, uses the current active session |

**Example Request:**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_step_out",
    "arguments": {}
  }
}
```

**Example Response:**

```json
{
  "success": true,
  "message": "Stepped out"
}
```

---

### ide_run_to_line

Run execution to a specific line.

**Use when:**
- Skipping to a specific line
- Running to a point of interest

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `file` | string | Yes | File path relative to project root |
| `line` | integer | Yes | 1-based line number to run to |
| `sessionId` | string | No | Session ID. If omitted, uses the current active session |

**Example Request:**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_run_to_line",
    "arguments": {
      "file": "src/main/java/com/example/App.java",
      "line": 100
    }
  }
}
```

**Example Response:**

```json
{
  "success": true,
  "message": "Running to line 100"
}
```

---

### ide_wait_for_pause

Wait for the debugger to pause (with timeout).

**Use when:**
- Waiting for a breakpoint to be hit
- Synchronizing with debugger state

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `timeoutSeconds` | integer | No | Timeout in seconds (default: 30, max: 300) |
| `sessionId` | string | No | Session ID. If omitted, uses the current active session |

**Example Request:**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_wait_for_pause",
    "arguments": {
      "timeoutSeconds": 60
    }
  }
}
```

**Example Response:**

```json
{
  "success": true,
  "isPaused": true,
  "pauseReason": "BREAKPOINT",
  "currentFile": "src/main/java/com/example/App.java",
  "currentLine": 42,
  "message": "Debugger paused at breakpoint"
}
```

**Pause Reasons:**
- `BREAKPOINT` - Hit a breakpoint
- `STEP` - After step operation
- `PAUSE` - User paused execution
- `EXCEPTION` - Exception thrown

---

### ide_get_stack_trace

Get the call stack of the current debug session.

**Use when:**
- Understanding the call hierarchy
- Navigating the stack

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `sessionId` | string | No | Session ID. If omitted, uses the current active session |

**Example Request:**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_get_stack_trace",
    "arguments": {}
  }
}
```

**Example Response:**

```json
{
  "frames": [
    {
      "index": 0,
      "file": "src/main/java/com/example/App.java",
      "line": 42,
      "method": "processUser",
      "class": "com.example.App",
      "isCurrent": true
    },
    {
      "index": 1,
      "file": "src/main/java/com/example/App.java",
      "line": 25,
      "method": "main",
      "class": "com.example.App",
      "isCurrent": false
    }
  ],
  "frameCount": 2
}
```

---

### ide_list_threads

List all threads in the debug session.

**Use when:**
- Viewing all running threads
- Switching between threads

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `sessionId` | string | No | Session ID. If omitted, uses the current active session |

**Example Request:**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_list_threads",
    "arguments": {}
  }
}
```

**Example Response:**

```json
{
  "threads": [
    {
      "id": 1,
      "name": "main",
      "status": "RUNNING",
      "isCurrent": true
    },
    {
      "id": 2,
      "name": "Thread-1",
      "status": "WAITING",
      "isCurrent": false
    }
  ],
  "currentThreadId": 1,
  "totalCount": 2
}
```

**Thread Status Values:**
- `RUNNING` - Currently executing
- `WAITING` - Waiting for monitor
- `TIMED_WAITING` - Waiting with timeout
- `BLOCKED` - Blocked on monitor
- `NEW` - Not yet started
- `TERMINATED` - Execution completed

---

### ide_select_stack_frame

Select a specific stack frame for variable inspection.

**Use when:**
- Inspecting variables in a different frame
- Navigating the call stack

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `frameIndex` | integer | Yes | 0-based frame index to select |
| `sessionId` | string | No | Session ID. If omitted, uses the current active session |

**Example Request:**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_select_stack_frame",
    "arguments": {
      "frameIndex": 1
    }
  }
}
```

**Example Response:**

```json
{
  "success": true,
  "selectedFrame": 1,
  "method": "main",
  "file": "src/main/java/com/example/App.java",
  "line": 25,
  "message": "Selected frame 1"
}
```

---

### ide_get_variables

Get variables in the current scope.

**Use when:**
- Inspecting variable values
- Understanding program state

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `scope` | string | No | Scope to inspect: `"local"`, `"global"`, or `"all"` (default: `"all"`) |
| `sessionId` | string | No | Session ID. If omitted, uses the current active session |

**Example Request:**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_get_variables",
    "arguments": {
      "scope": "local"
    }
  }
}
```

**Example Response:**

```json
{
  "variables": [
    {
      "name": "userId",
      "type": "String",
      "value": "\"user-123\"",
      "isPrimitive": false
    },
    {
      "name": "count",
      "type": "int",
      "value": "42",
      "isPrimitive": true
    }
  ],
  "scope": "local",
  "frameIndex": 0
}
```

---

### ide_set_variable

Set the value of a variable.

**Use when:**
- Modifying variable values during debugging
- Testing different scenarios

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `name` | string | Yes | Variable name to set |
| `value` | string | Yes | New value (as string representation) |
| `sessionId` | string | No | Session ID. If omitted, uses the current active session |

**Example Request:**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_set_variable",
    "arguments": {
      "name": "count",
      "value": "100"
    }
  }
}
```

**Example Response:**

```json
{
  "success": true,
  "name": "count",
  "oldValue": "42",
  "newValue": "100",
  "message": "Variable 'count' set to 100"
}
```

---

### ide_evaluate_expression

Evaluate an expression in the current debug context.

**Use when:**
- Evaluating expressions during debugging
- Testing code snippets
- Inspecting computed values

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `expression` | string | Yes | Expression to evaluate |
| `sessionId` | string | No | Session ID. If omitted, uses the current active session |

**Example Request:**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_evaluate_expression",
    "arguments": {
      "expression": "userService.findById(userId).getName()"
    }
  }
}
```

**Example Response:**

```json
{
  "success": true,
  "result": "\"John Doe\"",
  "type": "String",
  "isPrimitive": false,
  "message": "Expression evaluated successfully"
}
```

---

### ide_get_source_context

Get source code context around the current execution position.

**Use when:**
- Viewing code around current line
- Understanding execution context

**Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `contextLines` | integer | No | Number of lines before and after (default: 5, max: 50) |
| `sessionId` | string | No | Session ID. If omitted, uses the current active session |

**Example Request:**

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_get_source_context",
    "arguments": {
      "contextLines": 3
    }
  }
}
```

**Example Response:**

```json
{
  "file": "src/main/java/com/example/App.java",
  "currentLine": 42,
  "source": "39:     }\n40: \n41:     public void processUser(String id) {\n42: -->     User user = userService.findById(id);\n43:         if (user == null) {\n44:             throw new RuntimeException(\"User not found\");\n45:         }",
  "contextLines": 3
}
```

---

## Error Handling

### JSON-RPC Standard Errors

| Code | Name | When It Occurs |
|------|------|----------------|
| -32700 | Parse Error | Invalid JSON in request |
| -32600 | Invalid Request | Missing required JSON-RPC fields |
| -32601 | Method Not Found | Unknown tool or method name |
| -32602 | Invalid Params | Missing or invalid parameters |
| -32603 | Internal Error | Unexpected server error |

### Custom Errors

| Code | Name | When It Occurs |
|------|------|----------------|
| -32001 | Index Not Ready | IDE is indexing (dumb mode) |
| -32002 | File Not Found | Specified file doesn't exist |
| -32003 | Symbol Not Found | No symbol at the specified position |
| -32004 | Refactoring Conflict | Refactoring cannot be completed |

### Example Error Response

```json
{
  "jsonrpc": "2.0",
  "id": 1,
  "error": {
    "code": -32001,
    "message": "IDE is in dumb mode, indexes not available. Please wait for indexing to complete."
  }
}
```

### Handling Dumb Mode

Before calling index-dependent tools, you can check the index status:

```json
{
  "method": "tools/call",
  "params": {
    "name": "ide_index_status",
    "arguments": {}
  }
}
```

If `isDumbMode` is `true`, wait and retry later.
