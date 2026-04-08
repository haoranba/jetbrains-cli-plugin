"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.diagnosticsCommand = diagnosticsCommand;
const chalk_1 = __importDefault(require("chalk"));
const client_js_1 = require("../client.js");
const utils_js_1 = require("../utils.js");
function diagnosticsCommand(program) {
    program
        .command("diagnostics")
        .description("Get code diagnostics/analysis for a file or project")
        .option("--file <file>", "File path (optional, omit for project-wide)")
        .option("--severity <severity>", "Filter by severity: error, warning, info")
        .option("--include-build-errors", "Include build errors", false)
        .option("--include-test-results", "Include test results", false)
        .action(async (options, cmd) => {
        const opts = cmd.parent.opts();
        const client = new client_js_1.JetbrainsClient({
            host: opts.host,
            port: parseInt(opts.port),
        });
        const args = {
            includeBuildErrors: options.includeBuildErrors,
            includeTestResults: options.includeTestResults,
        };
        if (options.file)
            args.file = options.file;
        if (options.severity)
            args.severity = options.severity;
        const response = await client.callTool("ide_diagnostics", args);
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
            console.log(text);
        }
    });
}
//# sourceMappingURL=diagnostics.js.map