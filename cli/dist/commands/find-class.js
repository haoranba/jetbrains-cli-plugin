"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.findClassCommand = findClassCommand;
const chalk_1 = __importDefault(require("chalk"));
const client_js_1 = require("../client.js");
const utils_js_1 = require("../utils.js");
function findClassCommand(program) {
    program
        .command("find-class")
        .description("Search for classes/interfaces by name")
        .requiredOption("--query <query>", "Class name to search for")
        .option("--match-mode <mode>", "Match mode: substring, prefix, exact", "substring")
        .option("--max-results <n>", "Maximum results to return", "100")
        .action(async (options, cmd) => {
        const opts = cmd.parent.opts();
        const client = new client_js_1.JetbrainsClient({
            host: opts.host,
            port: parseInt(opts.port),
        });
        const args = {
            query: options.query,
            matchMode: options.matchMode,
            maxResults: parseInt(options.maxResults),
        };
        const response = await client.callTool("ide_find_class", args);
        if (opts.json) {
            console.log(JSON.stringify(response, null, 2));
            return;
        }
        printResult(response);
    });
}
function printResult(response) {
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
        console.log(text);
    }
}
//# sourceMappingURL=find-class.js.map