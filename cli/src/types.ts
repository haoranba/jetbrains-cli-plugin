// Tool definition schema
export interface ToolDefinition {
  name: string;
  description: string;
  inputSchema: {
    type: string;
    properties: Record<string, unknown>;
    required?: string[];
  };
}

// Tool call request
export interface ToolCallRequest {
  jsonrpc: "2.0";
  id: number;
  method: "tools/call";
  params: {
    name: string;
    arguments: Record<string, unknown>;
  };
}

// Tool call response
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

// Content block types
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

// Tools list response
export interface ToolsListResponse {
  tools: ToolDefinition[];
}

// Health check response
export interface HealthResponse {
  status: string;
  version: string;
  port: number;
}

// CLI options
export interface CliOptions {
  host: string;
  port: number;
  json: boolean;
}

// Reference result for formatting
export interface ReferenceResult {
  file: string;
  line: number;
  column: number;
  text: string;
}

// Type guard for TextContent
export function isTextContent(block: ContentBlock): block is TextContent {
  return block.type === "text";
}