"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.safeDeleteCommand = safeDeleteCommand;
const chalk_1 = __importDefault(require("chalk"));
const client_js_1 = require("../client.js");
const utils_js_1 = require("../utils.js");
function safeDeleteCommand(program) {
    program
        .command("safe-delete")
        .description("Safely delete an element (Java/Kotlin only)")
        .option("--file <file>", "File path")
        .option("--line <line>", "Line number (1-based)")
        .option("--column <column>", "Column number (1-based)")
        .action(async (options, cmd) => {
        const opts = cmd.parent.opts();
        const client = new client_js_1.JetbrainsClient({
            host: opts.host,
            port: parseInt(opts.port),
        });
        const args = {};
        if (options.file)
            args.file = options.file;
        if (options.line)
            args.line = parseInt(options.line);
        if (options.column)
            args.column = parseInt(options.column);
        const response = await client.callTool("ide_refactor_safe_delete", args);
        if (opts.json) {
            console.log(JSON.stringify(response, null, 2));
            return;
        }
        if (response.error) {
            console.error(chalk_1.default.red(`Error: ${response.error.message}`));
            process.exit(1);
        }
        if (response.result?.isError) {
            const text = (0, utils_js_1.extractFirstText)(response.result.content);
            console.error(chalk_1.default.red(text || "Unknown error"));
            process.exit(1);
        }
        const text = (0, utils_js_1.extractFirstText)(response.result?.content ?? []);
        if (text) {
            console.log(chalk_1.default.green(text));
        }
    });
}
//# sourceMappingURL=safe-delete.js.map