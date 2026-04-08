"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.moveFileCommand = moveFileCommand;
const chalk_1 = __importDefault(require("chalk"));
const client_js_1 = require("../client.js");
const utils_js_1 = require("../utils.js");
function moveFileCommand(program) {
    program
        .command("move-file")
        .description("Move a file to a new directory")
        .requiredOption("--file <file>", "File path to move")
        .requiredOption("--new-directory <dir>", "Target directory")
        .option("--update-refs <bool>", "Update references (default: true)", "true")
        .action(async (options, cmd) => {
        const opts = cmd.parent.opts();
        const client = new client_js_1.JetbrainsClient({
            host: opts.host,
            port: parseInt(opts.port),
        });
        const args = {
            file: options.file,
            newDirectory: options.newDirectory,
            updateReferences: options.updateRefs === "true",
        };
        const response = await client.callTool("ide_move_file", args);
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
//# sourceMappingURL=move-file.js.map