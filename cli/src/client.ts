import {
  ToolCallResponse,
  ToolsListResponse,
  HealthResponse,
  ToolCallRequest,
} from "./types.js";

const DEFAULT_HOST = "127.0.0.1";
const DEFAULT_PORT = 29170;

export interface ClientOptions {
  host?: string;
  port?: number;
}

export class JetbrainsClient {
  private baseUrl: string;

  constructor(options: ClientOptions = {}) {
    const host = options.host ?? DEFAULT_HOST;
    const port = options.port ?? DEFAULT_PORT;
    this.baseUrl = `http://${host}:${port}`;
  }

  /**
   * Call a tool on the JetBrains server
   */
  async callTool(
    toolName: string,
    args: Record<string, unknown>
  ): Promise<ToolCallResponse> {
    const request: ToolCallRequest = {
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
      throw new Error(
        `Server returned ${response.status}: ${response.statusText}`
      );
    }

    return (await response.json()) as ToolCallResponse;
  }

  /**
   * Get the list of available tools
   */
  async listTools(): Promise<ToolsListResponse> {
    const response = await fetch(`${this.baseUrl}/api/tools`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    });

    if (!response.ok) {
      throw new Error(
        `Server returned ${response.status}: ${response.statusText}`
      );
    }

    return (await response.json()) as ToolsListResponse;
  }

  /**
   * Check server health
   */
  async health(): Promise<HealthResponse> {
    const response = await fetch(`${this.baseUrl}/api/health`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    });

    if (!response.ok) {
      throw new Error(
        `Server returned ${response.status}: ${response.statusText}`
      );
    }

    return (await response.json()) as HealthResponse;
  }

  /**
   * Check if server is reachable
   */
  async ping(): Promise<boolean> {
    try {
      await this.health();
      return true;
    } catch {
      return false;
    }
  }
}

/**
 * Create a client and verify server connection
 */
export async function createClient(
  options: ClientOptions = {}
): Promise<JetbrainsClient> {
  const client = new JetbrainsClient(options);

  const isReachable = await client.ping();
  if (!isReachable) {
    const host = options.host ?? DEFAULT_HOST;
    const port = options.port ?? DEFAULT_PORT;
    throw new Error(
      `Cannot connect to JetBrains server at ${host}:${port}. Is the plugin running?`
    );
  }

  return client;
}