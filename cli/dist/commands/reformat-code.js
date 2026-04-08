"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.reformatCodeCommand = reformatCodeCommand;
const chalk_1 = __importDefault(require("chalk"));
const client_js_1 = require("../client.js");
const utils_js_1 = require("../utils.js");
function reformatCodeCommand(program) {
    program
        .command("reformat-code")
        .description("Reformat code using project code style")
        .option("--file <file>", "File path (optional, uses active file if omitted)")
        .option("--optimize-imports", "Optimize imports after reformatting", false)
        .option("--rearange", "Rearrange code elements", false)
        .action(async (options, cmd) => {
        const opts = cmd.parent.opts();
        const client = new client_js_1.JetbrainsClient({
            host: opts.host,
            port: parseInt(opts.port),
        });
        const args = {
            optimizeImports: options.optimizeImports,
            rearrange: options.rearange,
        };
        if (options.file)
            args.file = options.file;
        const response = await client.callTool("ide_reformat_code", args);
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
//# sourceMappingURL=reformat-code.js.map