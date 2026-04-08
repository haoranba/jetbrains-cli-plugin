import { ContentBlock, TextContent } from "./types.js";

/**
 * Extracts text content from a ContentBlock array.
 * Filters out non-text blocks and returns the concatenated text.
 */
export function extractText(content: ContentBlock[]): string {
  return content
    .filter((block): block is TextContent => block.type === "text")
    .map((block) => block.text)
    .join("\n");
}

/**
 * Extracts the first text content from a ContentBlock array.
 * Returns null if no text block is found.
 */
export function extractFirstText(content: ContentBlock[]): string | null {
  const textBlock = content.find((block): block is TextContent => block.type === "text");
  return textBlock?.text ?? null;
}