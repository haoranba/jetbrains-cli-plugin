# 设计文档：jetbrains-index-mcp-plugin → CLI 项目改造

**日期**：2026-04-09  
**状态**：已批准

---

## 背景与目标

将现有的 `jetbrains-index-mcp-plugin` 从 MCP server 模式改造为 **skill + CLI** 模式，使 Claude Code 等 AI 编码工具可以通过运行命令行工具来操作 JetBrains IDE，而无需 MCP 协议。

**使用链路：**
```
Claude Code
  → 读取 skill（描述可用命令）
  → 执行 jetbrains-cli <command> [options]
  → CLI 调用 JetBrains 本地 HTTP server（JSON-RPC 2.0）
  → 返回结果给 Claude Code
```

**动机：**
- 比 MCP 更灵活，未来可扩展 IDE Debug 等功能
- CLI 工具对人和 AI 都友好
- 去除 MCP 协议包袱（握手、session 管理、SSE 传输）

---

## 整体架构

```
┌─────────────────────────────────┐     POST /api         ┌──────────────────────┐
│     JetBrains IDE               │ ◄──── JSON-RPC 2.0 ── │  jetbrains-cli       │
│                                 │                         │  (TypeScript/Node)   │
│  Plugin: Ktor CIO HTTP Server   │                         │                      │
│  port: 29170 (localhost only)   │ ─── JSON response ────► │  → 人类可读输出       │
│                                 │                         │  → --json 原始输出    │
└─────────────────────────────────┘                         └──────────────────────┘
```

**两个项目（同一 repo）：**
- `jetbrains-index-mcp-plugin/` — Kotlin plugin，简化 server
- `cli/` — TypeScript CLI

**注意：改造后不再兼容 MCP 客户端（Cursor、Claude Desktop 等）。**

---

## 第一部分：Plugin 侧改动

### 要删除的文件
- `server/transport/KtorSseSessionManager.kt`
- `server/transport/StreamableHttpSessionManager.kt`

### 要简化的文件

**`server/transport/KtorMcpServer.kt`** → 重命名为 `KtorServer.kt`，只保留：
```
POST /api      → 无状态 JSON-RPC 2.0 请求处理，直接返回响应
GET  /api/tools → 返回所有工具列表（JSON）
GET  /api/health → {"status": "ok", "port": 29170}
```

删除：
- SSE 路由（`/index-mcp/sse`）
- Streamable HTTP 路由（`/index-mcp/streamable-http`）
- CORS preflight 处理（或简化为只允许 localhost）
- session 验证逻辑
- `handleSseRequest`、`handleStreamableHttpPostRequest`、`handleStreamableHttpDeleteRequest` 等所有方法

**`server/JsonRpcHandler.kt`** → 只保留三个方法路由：
- `tools/list` — 返回工具定义列表
- `tools/call` — 执行工具
- `ping` — 心跳

删除：
- `processInitialize()`
- `NOTIFICATIONS_INITIALIZED` 处理

**`server/McpServerService.kt`** → 移除：
- `sseSessionManager` 字段及相关调用
- `streamableHttpSessionManager` 字段及相关调用
- `getLegacySseUrl()`、`getServerUrl()`（MCP 专用 URL）
- `ServerStatusInfo` 中的 MCP 相关字段

### 不动的内容
- `tools/` 目录下所有工具实现（约 25 个工具）
- `handlers/` 语言处理器
- `settings/`、`ui/`、`history/`
- Ktor CIO server 核心

### 新增端点说明

**`POST /api`**
```json
// 请求
{
  "jsonrpc": "2.0",
  "id": 1,
  "method": "tools/call",
  "params": {
    "name": "ide_find_references",
    "arguments": {
      "project_path": "/path/to/project",
      "file": "src/Foo.kt",
      "line": 10,
      "column": 5
    }
  }
}

// 响应
{
  "jsonrpc": "2.0",
  "id": 1,
  "result": {
    "content": [{"type": "text", "text": "..."}],
    "isError": false
  }
}
```

**`GET /api/tools`**
```json
{
  "tools": [
    {"name": "ide_find_references", "description": "...", "inputSchema": {...}},
    ...
  ]
}
```

**`GET /api/health`**
```json
{"status": "ok", "version": "4.0.0", "port": 29170}
```

---

## 第二部分：TypeScript CLI

### 项目结构
```
cli/
├── src/
│   ├── index.ts              # 入口，commander 主程序
│   ├── client.ts             # HTTP client，封装 JSON-RPC 调用
│   ├── formatter.ts          # 人类可读输出格式化
│   ├── types.ts              # 共享类型定义（工具参数、响应结构）
│   └── commands/
│       ├── list-tools.ts
│       ├── find-usages.ts
│       ├── find-definition.ts
│       ├── find-class.ts
│       ├── find-file.ts
│       ├── search-text.ts
│       ├── read-file.ts
│       ├── diagnostics.ts
│       ├── index-status.ts
│       ├── sync-files.ts
│       ├── build-project.ts
│       ├── rename.ts
│       ├── move-file.ts
│       ├── reformat-code.ts
│       ├── optimize-imports.ts
│       ├── safe-delete.ts
│       ├── type-hierarchy.ts
│       ├── call-hierarchy.ts
│       ├── find-implementations.ts
│       ├── find-symbol.ts
│       ├── find-super-methods.ts
│       ├── file-structure.ts
│       ├── convert-java-to-kotlin.ts
│       ├── get-active-file.ts
│       └── open-file.ts
├── package.json
└── tsconfig.json
```

### 依赖
- `commander` — CLI 框架
- `chalk` — 终端颜色输出
- `node-fetch` 或原生 `fetch`（Node 18+）

### 全局选项
```
jetbrains-cli [options] <command>

Options:
  --host <host>   Server host (default: 127.0.0.1)
  --port <port>   Server port (default: 29170)
  --json          Output raw JSON
  --help          Show help
```

### 命令示例
```bash
# 查找引用
jetbrains-cli find-usages --file src/Foo.kt --line 10 --column 5

# 查找定义
jetbrains-cli find-definition --file src/Foo.kt --line 10 --column 5

# 查找类
jetbrains-cli find-class --query MyService

# 重命名
jetbrains-cli rename --file src/Foo.kt --line 10 --column 5 --new-name Bar

# 诊断信息
jetbrains-cli diagnostics --file src/Foo.kt
jetbrains-cli diagnostics  # 全局诊断

# 列出所有工具
jetbrains-cli list-tools

# JSON 输出
jetbrains-cli find-usages --file src/Foo.kt --line 10 --column 5 --json
```

### 输出格式

**人类可读（默认）：**
```
Found 3 references to Foo

src/main/Foo.kt:10:5   class Foo { ... }
src/test/FooTest.kt:3:8   import com.example.Foo
src/main/Bar.kt:25:12   val foo = Foo()
```

**JSON（--json）：**
```json
{
  "content": [{"type": "text", "text": "..."}],
  "isError": false
}
```

### client.ts 设计
```typescript
// 封装单次 JSON-RPC 调用
async function callTool(
  toolName: string,
  args: Record<string, unknown>,
  options: { host: string; port: number }
): Promise<ToolCallResult>

// 获取工具列表
async function listTools(options: { host: string; port: number }): Promise<Tool[]>
```

### 错误处理
- Server 未启动 → `Error: Cannot connect to JetBrains server at 127.0.0.1:29170. Is the plugin running?`
- 工具返回 isError=true → 打印错误内容，exit code 1
- JSON-RPC 错误 → 打印 error.message，exit code 1

---

## 第三部分：Skill 文件

**位置：** `jetbrains-index-mcp-plugin/src/main/resources/skill/jetbrains-cli/SKILL.md`

**内容结构：**
1. **触发条件** — 什么时候应该用 jetbrains-cli（代替 grep/ripgrep 做语义搜索）
2. **前提条件** — IDE 必须开着，plugin 必须运行，CLI 必须已安装
3. **常用命令速查** — 每个场景对应哪个命令
4. **完整命令参考** — 所有命令的参数说明
5. **使用示例** — 典型用法

**核心原则（写入 skill）：**
- 找引用/定义 → 用 `find-usages` / `find-definition`，不用 grep
- 检查错误 → 用 `diagnostics`，不用手动读文件
- 重命名 → 用 `rename`，不用 sed/手动替换
- IDE 正在索引时（`index-status` 返回 dumb mode）→ 等待再操作

---

## 不在本次范围内

- IDE Debug 相关功能（后续迭代）
- 多 IDE 同时运行的端口自动发现
- CLI 安装脚本 / npm 发布配置
- MCP 兼容层（明确不需要）

---

## 项目目录最终结构

```
jetbrains-cli/              ← repo 根目录（现有目录）
├── jetbrains-index-mcp-plugin/   ← Kotlin plugin（简化后）
│   └── src/main/resources/skill/jetbrains-cli/SKILL.md
└── cli/                    ← 新建 TypeScript CLI
    ├── src/
    ├── package.json
    └── tsconfig.json
```
