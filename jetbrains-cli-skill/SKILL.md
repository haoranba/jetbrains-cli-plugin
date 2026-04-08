---
name: jetbrains-cli
description: >
  CLI tool for JetBrains IDE code intelligence via MCP. TRIGGER when ANY of these MCP tools are available:
  ide_find_references, ide_find_definition, ide_find_class, ide_find_file, ide_search_text, ide_diagnostics,
  ide_refactor_rename, ide_move_file, ide_type_hierarchy, ide_call_hierarchy, ide_find_implementations.
  Use when searching for references, definitions, implementations, type hierarchy, call hierarchy,
  refactoring (rename, move), checking diagnostics, or building projects. PREFER over grep/ripgrep for
  semantic code search. Keywords: find usages, find references, find definition, rename, refactor,
  diagnostics, jetbrains, intellij, ide index.
---

# jetbrains-cli 使用指南

## 触发条件

在以下场景使用 `jetbrains-cli` 而不是 grep/ripgrep：

1. **语义搜索** - 查找引用、定义、实现、类型层次结构
2. **代码导航** - 基于 IDE 索引的快速搜索
3. **重构操作** - 重命名、移动文件、安全删除
4. **诊断检查** - 获取代码错误、警告信息
5. **项目构建** - 使用 IDE 构建系统编译项目

## 前提条件

1. **JetBrains IDE 必须运行中** - IntelliJ IDEA、PyCharm、WebStorm、GoLand 等
2. **Plugin 必须安装并运行** - "Index MCP Server" plugin
3. **CLI 必须已安装** - `jetbrains-cli` 命令可用

## 常用命令速查

| 场景 | 命令 |
|------|------|
| 查找符号引用 | `jetbrains-cli find-usages --file src/Foo.kt --line 10 --column 5` |
| 查找符号定义 | `jetbrains-cli find-definition --file src/Foo.kt --line 10 --column 5` |
| 按类名搜索 | `jetbrains-cli find-class --query MyService` |
| 按文件名搜索 | `jetbrains-cli find-file --query pom.xml` |
| 文本搜索 | `jetbrains-cli search-text --query "TODO"` |
| 重命名符号 | `jetbrains-cli rename --file src/Foo.kt --line 10 --column 5 --new-name Bar` |
| 移动文件 | `jetbrains-cli move-file --file src/Old.kt --new-directory src/new/package` |
| 检查错误 | `jetbrains-cli diagnostics --file src/Foo.kt` |
| 查看索引状态 | `jetbrains-cli index-status` |
| 同步文件 | `jetbrains-cli sync-files` |
| 构建项目 | `jetbrains-cli build-project` |
| 类型层次 | `jetbrains-cli type-hierarchy --file src/Foo.kt --line 10 --column 5` |
| 调用层次 | `jetbrains-cli call-hierarchy --file src/Foo.kt --line 10 --column 5` |
| 查找实现 | `jetbrains-cli find-implementations --file src/IFoo.kt --line 5 --column 10` |
| 查找父方法 | `jetbrains-cli find-super-methods --file src/Bar.kt --line 20 --column 15` |
| 文件结构 | `jetbrains-cli file-structure --file src/Foo.kt` |
| Java 转 Kotlin | `jetbrains-cli convert-java-to-kotlin --file src/Foo.java` |
| 格式化代码 | `jetbrains-cli reformat-code --file src/Foo.kt` |
| 优化导入 | `jetbrains-cli optimize-imports --file src/Foo.kt` |
| 安全删除 | `jetbrains-cli safe-delete --file src/Old.kt --line 10 --column 5` |
| 读取文件 | `jetbrains-cli read-file --file src/Foo.kt` |
| 打开文件 | `jetbrains-cli open-file --file src/Foo.kt --line 10` |
| 获取活动文件 | `jetbrains-cli get-active-file` |
| 列出工具 | `jetbrains-cli list-tools` |

## 完整命令参考

### 全局选项

```
jetbrains-cli [options] <command>

Options:
  --host <host>   Server host (default: 127.0.0.1)
  --port <port>   Server port (default: 29170)
  --json          Output raw JSON
  --help          Show help
```

### 导航命令

#### find-usages
查找符号的所有引用。
```bash
jetbrains-cli find-usages --file src/Foo.kt --line 10 --column 5
jetbrains-cli find-usages --language Java --symbol com.example.MyClass
```

#### find-definition
查找符号定义位置。
```bash
jetbrains-cli find-definition --file src/Foo.kt --line 10 --column 5
jetbrains-cli find-definition --language Kotlin --symbol MyFunction
```

#### find-class
按类名搜索类/接口。
```bash
jetbrains-cli find-class --query UserService --match-mode substring
```

#### find-file
按文件名搜索文件。
```bash
jetbrains-cli find-file --query pom.xml
```

#### find-symbol
按名称搜索符号（类、方法、字段）。
```bash
jetbrains-cli find-symbol --query "getConfig"
```

### 重构命令

#### rename
重命名符号或文件。
```bash
# 符号重命名
jetbrains-cli rename --file src/Foo.kt --line 10 --column 5 --new-name Bar

# 文件重命名
jetbrains-cli rename --file src/OldName.kt --new-name NewName.kt
```

#### move-file
移动文件到新目录。
```bash
jetbrains-cli move-file --file src/Old.kt --new-directory src/new/package
```

#### safe-delete
安全删除元素（Java/Kotlin）。
```bash
jetbrains-cli safe-delete --file src/Old.kt --line 10 --column 5
```

#### reformat-code
格式化代码。
```bash
jetbrains-cli reformat-code --file src/Foo.kt --optimize-imports
```

#### optimize-imports
优化导入。
```bash
jetbrains-cli optimize-imports --file src/Foo.kt
```

#### convert-java-to-kotlin
Java 转 Kotlin。
```bash
jetbrains-cli convert-java-to-kotlin --file src/Foo.java
```

### 分析命令

#### diagnostics
获取代码诊断信息。
```bash
# 项目范围
jetbrains-cli diagnostics

# 文件范围
jetbrains-cli diagnostics --file src/Foo.kt

# 包含构建错误
jetbrains-cli diagnostics --include-build-errors
```

#### type-hierarchy
获取类型层次结构。
```bash
jetbrains-cli type-hierarchy --file src/Foo.kt --line 10 --column 5
```

#### call-hierarchy
获取调用层次结构。
```bash
jetbrains-cli call-hierarchy --file src/Foo.kt --line 10 --column 5
```

#### find-implementations
查找实现。
```bash
jetbrains-cli find-implementations --file src/IFoo.kt --line 5 --column 10
```

#### find-super-methods
查找父方法。
```bash
jetbrains-cli find-super-methods --file src/Bar.kt --line 20 --column 15
```

#### file-structure
获取文件结构。
```bash
jetbrains-cli file-structure --file src/Foo.kt
```

### 项目命令

#### index-status
检查索引状态。
```bash
jetbrains-cli index-status
```
返回 "dumb mode" 表示 IDE 正在索引，需等待完成后操作。

#### sync-files
同步文件系统。
```bash
jetbrains-cli sync-files
```

#### build-project
构建项目。
```bash
jetbrains-cli build-project
jetbrains-cli build-project --project-path ./sub-module
```

### 编辑器命令

#### read-file
读取文件内容。
```bash
jetbrains-cli read-file --file src/Foo.kt
```

#### open-file
打开文件。
```bash
jetbrains-cli open-file --file src/Foo.kt --line 10 --column 5
```

#### get-active-file
获取当前活动文件。
```bash
jetbrains-cli get-active-file
```

### 其他命令

#### search-text
文本搜索。
```bash
jetbrains-cli search-text --query "TODO"
```

#### list-tools
列出所有工具。
```bash
jetbrains-cli list-tools
jetbrains-cli list-tools --json
```

## 使用示例

### 示例 1：重构函数名
```bash
# 1. 先查找引用
jetbrains-cli find-usages --file src/utils.kt --line 25 --column 10

# 2. 执行重命名
jetbrains-cli rename --file src/utils.kt --line 25 --column 10 --new-name newFunctionName

# 3. 验证重构结果
jetbrains-cli find-usages --file src/utils.kt --line 25 --column 10
```

### 示例 2：查找类的层次结构
```bash
# 1. 查找类
jetbrains-cli find-class --query BaseRepository

# 2. 查看类型层次
jetbrains-cli find-definition --file src/BaseRepository.kt --line 10 --column 7
jetbrains-cli type-hierarchy --file src/BaseRepository.kt --line 10 --column 7

# 3. 查找实现
jetbrains-cli find-implementations --file src/BaseRepository.kt --line 10 --column 7
```

### 示例 3：诊断和修复错误
```bash
# 1. 检查文件错误
jetbrains-cli diagnostics --file src/Broken.kt

# 2. 修复后重新检查
jetbrains-cli diagnostics --file src/Broken.kt
```

## 输出格式

### 人类可读（默认）
```
Found 3 references to Foo

src/main/Foo.kt:10:5   class Foo { ... }
src/test/FooTest.kt:3:8   import com.example.Foo
src/main/Bar.kt:25:12   val foo = Foo()
```

### JSON 输出
```bash
jetbrains-cli find-usages --file src/Foo.kt --line 10 --column 5 --json
```

```json
{
  "jsonrpc": "2.0",
  "id": 1234567890,
  "result": {
    "content": [{"type": "text", "text": "..."}],
    "isError": false
  }
}
```

## 错误处理

- **Server 未启动**: `Cannot connect to JetBrains server at 127.0.0.1:29170. Is the plugin running?`
- **工具错误**: 返回错误消息，exit code 1
- **JSON-RPC 错误**: 打印 error.message，exit code 1

## 核心原则

1. **找引用/定义** → 用 `find-usages` / `find-definition`，不用 grep
2. **检查错误** → 用 `diagnostics`，不用手动读文件
3. **重命名** → 用 `rename`，不用 sed/手动替换
4. **IDE 索引中** → 先用 `index-status` 检查，返回 dumb mode 时等待