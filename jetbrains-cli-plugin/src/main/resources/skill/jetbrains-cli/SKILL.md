---
name: jetbrains-cli
description: >
  Guide for using the jetbrains-cli CLI tool for code navigation, refactoring, debugging,
  and JAR/library exploration in JetBrains IDE projects.
  TRIGGER: When working on Java/Kotlin/Python/Go projects with JetBrains IDE open, or when
  user mentions debugging, finding classes, exploring JAR dependencies, or refactoring code.
  Use jetbrains-cli commands (via Bash) instead of grep/find/sed/read for ALL semantic code operations.
---

# jetbrains-cli 使用指南

JetBrains IDE 通过插件暴露语义级代码能力，`jetbrains-cli` 是访问这些能力的 CLI 工具。

## 核心规则

**对语义代码操作，始终优先用 `jetbrains-cli`，而不是内置工具。** IDE 理解代码结构、类型、继承和引用；grep/read/mv 只看文本。

## 前置检查

```bash
# 确认 IDE 已完成索引（大多数命令需要 smart mode）
jetbrains-cli index-status
# isDumbMode: true → 等几秒后重试

# 在 IDE 外创建/修改文件后，先同步
jetbrains-cli sync-files
```

## 决策表

| 任务 | 用 CLI | 别用 |
|------|--------|------|
| 找方法/类/变量的所有引用 | `find-usages --file f --line l --column c` | grep（漏别名、子类覆写） |
| 跳转到定义 | `find-definition --file f --line l --column c` | grep（无法穿透泛型/导入） |
| 按名搜索类（项目内） | `find-class --query UserService` | Glob |
| 搜索 JAR/依赖库中的类 | `find-class --query ArrayList --include-libraries` | 无法用 grep |
| 读 JAR/库的反编译源码 | `read-file --file <find-class 返回的 jar 路径>` | Read（只能读本地文件） |
| 类型继承层级 | `type-hierarchy --symbol com.example.MyClass` 或 `--file f --line l --column c` | 无等价工具 |
| 调用链（谁调用我） | `call-hierarchy --file f --line l --column c --direction callers` | 无等价工具 |
| 找接口实现 | `find-implementations --file f --line l --column c` | 无等价工具 |
| 找父方法 | `find-super-methods --file f --line l --column c` | 无等价工具 |
| 重命名符号 | `rename --file f --line l --column c --new-name newName` | sed（破坏代码） |
| 移动文件 | `move-file --file f --destination dir/` | mv（不更新 import） |
| 安全删除 | `safe-delete --file f` | rm（不检查引用） |
| 格式化代码 | `reformat-code --file f` | — |
| 优化 import | `optimize-imports --file f` | — |
| 检查编译错误/警告 | `diagnostics --file f` | 无等价工具 |
| 构建项目 | `build-project` | — |
| 设置断点 | `debug set-breakpoint --file f --line l` | 无等价工具 |
| 启动调试会话 | `debug start-session --config MyApp` | 无等价工具 |
| 检查调试变量 | `debug variables` | 无等价工具 |
| 求值调试表达式 | `debug evaluate --expression "obj.field"` | 无等价工具 |
| 按名搜文件 | `find-file --query App.java` | Glob 也可以 |
| 正则搜索文本 | Grep | `search-text`（不支持正则） |

## JAR / 库探索工作流

```bash
# 1. 在所有依赖 JAR 中按名查找类（返回结果含 jar 路径）
jetbrains-cli find-class --query EnableMongo --include-libraries

# 2. 用返回的路径读反编译源码
jetbrains-cli read-file --file "jar:///path/to/lib.jar!/com/example/EnableMongo.class"

# 3. 直接跳到库方法定义（Java 支持 symbol 格式）
jetbrains-cli find-definition --language Java --symbol com.example.Foo#bar
```

## Debug 工作流

```bash
# 1. 列出可用运行配置
jetbrains-cli debug list-configs

# 2. 设置断点（可加条件）
jetbrains-cli debug set-breakpoint --file src/main/java/com/example/Foo.java --line 42
jetbrains-cli debug set-breakpoint --file src/main/java/com/example/Foo.java --line 42 --condition "x > 0"

# 3. 启动调试会话
jetbrains-cli debug start-session --config "MyApp"

# 4. 等待程序暂停在断点
jetbrains-cli debug wait-for-pause --timeout 30000

# 5. 检查现场
jetbrains-cli debug stack                              # 查看调用栈
jetbrains-cli debug variables                          # 查看当前帧变量
jetbrains-cli debug evaluate --expression "user.getName()"  # 求值表达式

# 6. 单步执行
jetbrains-cli debug step-over   # 跳过
jetbrains-cli debug step-into   # 进入方法
jetbrains-cli debug step-out    # 跳出方法

# 7. 继续或停止
jetbrains-cli debug continue
jetbrains-cli debug stop-session
```

## 参数规则

1. **文件路径是相对路径**：相对项目根目录，如 `src/main/java/App.java`，不是绝对路径
2. **行列从 1 开始**：第一行 = `--line 1`，第一列 = `--column 1`
3. **`--column` 指向符号名首字母**：`public void myMethod()` 中，column 指向 `m`
4. **多项目时加 `--project-path`**：单项目可省略；多项目必须指定项目根目录绝对路径
5. **symbol 格式**（仅 Java）：`com.example.ClassName` 或 `com.example.ClassName#methodName`

## 常见错误

1. **用 grep 代替 `find-usages`**：grep 找文本，不找语义。漏掉别名导入、子类覆写
2. **用 sed 代替 `rename`**：不更新所有引用、getter/setter、覆写方法
3. **用 mv 代替 `move-file`**：不更新 import 和 package 声明
4. **忘记检查索引状态**：IDE 刚打开处于 dumb mode，先跑 `index-status`
5. **用绝对路径**：`--file` 参数要用相对路径
6. **修改文件后搜索结果不对**：用 Write/Edit 修改文件后先跑 `sync-files`
7. **`search-text` 用正则**：该命令只支持精确词匹配，正则用 Grep

## 全局选项

```
jetbrains-cli [--host <host>] [--port <port>] [--project-path <path>] [--json] <command>

# 默认：host=127.0.0.1, port=29170
# 多项目示例：
jetbrains-cli --project-path /Users/dev/myproject find-class --query UserService
```
