"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.extractText = extractText;
exports.extractFirstText = extractFirstText;
/**
 * Extracts text content from a ContentBlock array.
 * Filters out non-text blocks and returns the concatenated text.
 */
function extractText(content) {
    return content
        .filter((block) => block.type === "text")
        .map((block) => block.text)
        .join("\n");
}
/**
 * Extracts the first text content from a ContentBlock array.
 * Returns null if no text block is found.
 */
function extractFirstText(content) {
    const textBlock = content.find((block) => block.type === "text");
    return textBlock?.text ?? null;
}
//# sourceMappingURL=utils.js.map