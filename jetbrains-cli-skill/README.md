# @antskill/jetbrains-cli

CLI tool for JetBrains IDE code intelligence via MCP - semantic search, navigation, refactoring, and diagnostics.

## Installation

```bash
tnpm install -g @antskill/jetbrains-cli
```

## Prerequisites

1. **JetBrains IDE must be running** - IntelliJ IDEA, PyCharm, WebStorm, GoLand, etc.
2. **Plugin must be installed** - "Index MCP Server" plugin
3. **CLI must be installed** - `jetbrains-cli` command available

## Features

- **Semantic Search** - Find references, definitions, implementations, type hierarchy
- **Code Navigation** - Fast search based on IDE index
- **Refactoring** - Rename, move files, safe delete
- **Diagnostics** - Get code errors and warnings
- **Project Build** - Compile using IDE build system

## Quick Reference

| Scenario | Command |
|----------|---------|
| Find references | `jetbrains-cli find-usages --file src/Foo.kt --line 10 --column 5` |
| Find definition | `jetbrains-cli find-definition --file src/Foo.kt --line 10 --column 5` |
| Search class | `jetbrains-cli find-class --query MyService` |
| Search file | `jetbrains-cli find-file --query pom.xml` |
| Rename symbol | `jetbrains-cli rename --file src/Foo.kt --line 10 --column 5 --new-name Bar` |
| Check errors | `jetbrains-cli diagnostics --file src/Foo.kt` |
| Index status | `jetbrains-cli index-status` |

## License

MIT