"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.JetbrainsClient = void 0;
exports.createClient = createClient;
const DEFAULT_HOST = "127.0.0.1";
const DEFAULT_PORT = 29170;
class JetbrainsClient {
    baseUrl;
    constructor(options = {}) {
        const host = options.host ?? DEFAULT_HOST;
        const port = options.port ?? DEFAULT_PORT;
        this.baseUrl = `http://${host}:${port}`;
    }
    /**
     * Call a tool on the JetBrains server
     */
    async callTool(toolName, args) {
        const request = {
            jsonrpc: "2.0",
            id: Date.now(),
            method: "tools/call",
            params: {
                name: toolName,
                arguments: args,
            },
        };
        const response = await fetch(`${this.baseUrl}/api`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(request),
        });
        if (!response.ok) {
            throw new Error(`Server returned ${response.status}: ${response.statusText}`);
        }
        return (await response.json());
    }
    /**
     * Get the list of available tools
     */
    async listTools() {
        const response = await fetch(`${this.baseUrl}/api/tools`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
            },
        });
        if (!response.ok) {
            throw new Error(`Server returned ${response.status}: ${response.statusText}`);
        }
        return (await response.json());
    }
    /**
     * Check server health
     */
    async health() {
        const response = await fetch(`${this.baseUrl}/api/health`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
            },
        });
        if (!response.ok) {
            throw new Error(`Server returned ${response.status}: ${response.statusText}`);
        }
        return (await response.json());
    }
    /**
     * Check if server is reachable
     */
    async ping() {
        try {
            await this.health();
            return true;
        }
        catch {
            return false;
        }
    }
}
exports.JetbrainsClient = JetbrainsClient;
/**
 * Create a client and verify server connection
 */
async function createClient(options = {}) {
    const client = new JetbrainsClient(options);
    const isReachable = await client.ping();
    if (!isReachable) {
        const host = options.host ?? DEFAULT_HOST;
        const port = options.port ?? DEFAULT_PORT;
        throw new Error(`Cannot connect to JetBrains server at ${host}:${port}. Is the plugin running?`);
    }
    return client;
}
//# sourceMappingURL=client.js.map