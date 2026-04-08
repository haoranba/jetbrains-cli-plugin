export interface ToolDefinition {
    name: string;
    description: string;
    inputSchema: {
        type: string;
        properties: Record<string, unknown>;
        required?: string[];
    };
}
export interface ToolCallRequest {
    jsonrpc: "2.0";
    id: number;
    method: "tools/call";
    params: {
        name: string;
        arguments: Record<string, unknown>;
    };
}
export interface ToolCallResponse {
    jsonrpc: "2.0";
    id: number;
    result?: {
        content: ContentBlock[];
        isError?: boolean;
    };
    error?: {
        code: number;
        message: string;
    };
}
export type ContentBlock = TextContent | ImageContent;
export interface TextContent {
    type: "text";
    text: string;
}
export interface ImageContent {
    type: "image";
    data: string;
    mimeType: string;
}
export interface ToolsListResponse {
    tools: ToolDefinition[];
}
export interface HealthResponse {
    status: string;
    version: string;
    port: number;
}
export interface CliOptions {
    host: string;
    port: number;
    json: boolean;
}
export interface ReferenceResult {
    file: string;
    line: number;
    column: number;
    text: string;
}
export declare function isTextContent(block: ContentBlock): block is TextContent;
//# sourceMappingURL=types.d.ts.map