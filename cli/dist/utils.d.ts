import { ContentBlock } from "./types.js";
/**
 * Extracts text content from a ContentBlock array.
 * Filters out non-text blocks and returns the concatenated text.
 */
export declare function extractText(content: ContentBlock[]): string;
/**
 * Extracts the first text content from a ContentBlock array.
 * Returns null if no text block is found.
 */
export declare function extractFirstText(content: ContentBlock[]): string | null;
//# sourceMappingURL=utils.d.ts.map