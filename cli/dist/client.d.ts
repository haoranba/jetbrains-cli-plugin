import { ToolCallResponse, ToolsListResponse, HealthResponse } from "./types.js";
export interface ClientOptions {
    host?: string;
    port?: number;
}
export declare class JetbrainsClient {
    private baseUrl;
    constructor(options?: ClientOptions);
    /**
     * Call a tool on the JetBrains server
     */
    callTool(toolName: string, args: Record<string, unknown>): Promise<ToolCallResponse>;
    /**
     * Get the list of available tools
     */
    listTools(): Promise<ToolsListResponse>;
    /**
     * Check server health
     */
    health(): Promise<HealthResponse>;
    /**
     * Check if server is reachable
     */
    ping(): Promise<boolean>;
}
/**
 * Create a client and verify server connection
 */
export declare function createClient(options?: ClientOptions): Promise<JetbrainsClient>;
//# sourceMappingURL=client.d.ts.map