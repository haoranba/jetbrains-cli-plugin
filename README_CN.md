# JetBrains CLI

为 AI 编码助手提供来自 JetBrains IDE 的语义代码智能能力 —— 查找引用、跳转定义、安全重构等。

## 项目结构

```
jetbrains-cli-plugin/   ← JetBrains IDE 插件（Kotlin）
cli/                    ← 命令行客户端（Node.js / TypeScript）
```

**插件**运行在 JetBrains IDE 内部，暴露一个 JSON-RPC 2.0 HTTP API。**CLI** 是一个轻量客户端，供你或 AI 助手在终端调用该 API。

## 快速开始

### 1. 安装插件

从 IDE 插件市场安装 **JetBrains CLI**：

`Settings` → `Plugins` → `Marketplace` → 搜索 **JetBrains CLI**

或从 [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/29174-jetbrains-cli-plugin) 手动安装。

打开项目后，服务会根据 IDE 类型自动在对应端口启动：

| IDE | 默认端口 |
|-----|---------|
| IntelliJ IDEA | 29170 |
| Android Studio | 29171 |
| PyCharm | 29172 |
| WebStorm | 29173 |
| GoLand | 29174 |
| PhpStorm | 29175 |

### 2. 安装 CLI

```bash
npm install -g jetbrains-cli
```

或从源码构建：

```bash
cd cli
npm install && npm run build && npm link
```

### 3. 开始使用

```bash
# 检查插件是否运行
curl http://127.0.0.1:29170/api/health

# 查找类
jetbrains-cli find-class --query UserService

# 查找引用
jetbrains-cli find-usages --file src/Foo.kt --line 10 --column 5

# 重命名符号
jetbrains-cli rename --file src/Foo.kt --line 10 --column 5 --new-name Bar
```

## 可用命令

### 导航
| 命令 | 说明 |
|------|------|
| `find-class` | 按名称搜索类/接口（支持 camelCase 缩写匹配） |
| `find-file` | 按名称搜索文件 |
| `find-symbol` | 按名称搜索符号（类、方法、字段） |
| `find-usages` | 查找符号的所有引用 |
| `find-definition` | 跳转到符号定义 |
| `find-implementations` | 查找接口或抽象方法的实现 |
| `find-super-methods` | 查找继承链中的父方法 |
| `type-hierarchy` | 查看类型继承链 |
| `call-hierarchy` | 追踪方法的调用者或被调用者 |
| `file-structure` | 查看文件结构（类似 IDE Structure 视图） |

### 重构
| 命令 | 说明 |
|------|------|
| `rename` | 重命名符号并自动更新所有引用 |
| `move-file` | 移动文件并自动更新 import 和引用 |
| `safe-delete` | 先检查使用情况再安全删除（Java/Kotlin） |
| `reformat-code` | 按项目代码风格格式化 |
| `optimize-imports` | 优化导入 |
| `convert-java-to-kotlin` | Java 转 Kotlin |

### 分析
| 命令 | 说明 |
|------|------|
| `diagnostics` | 获取文件的错误和警告 |
| `build-project` | 触发 IDE 构建 |
| `index-status` | 检查 IDE 索引是否完成 |
| `sync-files` | 外部文件变更后同步 VFS/PSI 缓存 |

### 编辑器
| 命令 | 说明 |
|------|------|
| `read-file` | 读取文件内容，支持从 JAR 读取库源码 |
| `open-file` | 在编辑器中打开文件 |
| `get-active-file` | 获取当前活跃的编辑器文件 |

## 全局选项

```
jetbrains-cli [options] <command>

--host <host>          服务器地址（默认：127.0.0.1）
--port <port>          服务器端口（默认：29170）
--project-path <path>  多项目场景下必须指定项目路径
--json                 输出原始 JSON
```

**同时打开了多个项目？** 需要指定 `--project-path`：

```bash
jetbrains-cli --project-path /Users/dev/myproject find-class --query UserService
```

若不指定且有多个项目打开，会返回错误并列出所有可用的项目路径。

## API

插件暴露简单的 JSON-RPC 2.0 接口：

| 端点 | 方法 | 说明 |
|------|------|------|
| `/api` | POST | JSON-RPC 2.0 请求 |
| `/api/tools` | GET | 列出所有可用工具 |
| `/api/health` | GET | 健康检查 |

```bash
# 健康检查
curl http://127.0.0.1:29170/api/health
# {"status":"ok","version":"4.0.0","port":29170}

# 调用工具
curl -X POST http://127.0.0.1:29170/api \
  -H "Content-Type: application/json" \
  -d '{"jsonrpc":"2.0","id":1,"method":"tools/call","params":{"name":"ide_find_class","arguments":{"query":"UserService"}}}'
```

## 支持的 IDE 和语言

**已测试：** IntelliJ IDEA、Android Studio、PyCharm、WebStorm、GoLand、RustRover、PhpStorm

**支持语言：** Java、Kotlin、Python、JavaScript/TypeScript、Go、PHP、Rust

## 环境要求

- JetBrains IDE 2025.1+
- Node.js 18+（CLI 需要）

## 文档

- [`cli/README.md`](cli/README.md) — CLI 使用说明和命令参考
- [`jetbrains-cli-plugin/USAGE.md`](jetbrains-cli-plugin/USAGE.md) — 完整工具参数参考
- [`jetbrains-cli-plugin/README.md`](jetbrains-cli-plugin/README.md) — 插件文档

## License

[MIT](LICENSE)
