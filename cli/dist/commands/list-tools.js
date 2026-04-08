"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.listToolsCommand = listToolsCommand;
const chalk_1 = __importDefault(require("chalk"));
const client_js_1 = require("../client.js");
function listToolsCommand(program) {
    program
        .command("list-tools")
        .description("List all available tools")
        .action(async (_, cmd) => {
        const opts = cmd.parent.opts();
        const client = new client_js_1.JetbrainsClient({
            host: opts.host,
            port: parseInt(opts.port),
        });
        const result = await client.listTools();
        if (opts.json) {
            console.log(JSON.stringify(result, null, 2));
            return;
        }
        console.log(chalk_1.default.bold(`Available tools (${result.tools.length}):`));
        console.log();
        for (const tool of result.tools) {
            console.log(chalk_1.default.cyan(tool.name));
            console.log(`  ${tool.description}`);
            console.log();
        }
    });
}
//# sourceMappingURL=list-tools.js.map