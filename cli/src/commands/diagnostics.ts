import { Command } from "commander";
import chalk from "chalk";
import { JetbrainsClient } from "../client.js";
import { extractFirstText } from "../utils.js";

export function diagnosticsCommand(program: Command) {
  program
    .command("diagnostics")
    .description("Get code diagnostics/analysis for a file or project")
    .option("--file <file>", "File path (optional, omit for project-wide)")
    .option("--severity <severity>", "Filter by severity: error, warning, info")
    .option("--include-build-errors", "Include build errors", false)
    .option("--include-test-results", "Include test results", false)
    .action(async (options, cmd) => {
      const opts = cmd.parent.opts();
      const client = new JetbrainsClient({
        host: opts.host,
        port: parseInt(opts.port),
      });

      const args: Record<string, unknown> = {
        includeBuildErrors: options.includeBuildErrors,
        includeTestResults: options.includeTestResults,
      };

      if (options.file) args.file = options.file;
      if (options.severity) args.severity = options.severity;

      const response = await client.callTool("ide_diagnostics", args);

      if (opts.json) {
        console.log(JSON.stringify(response, null, 2));
        return;
      }

      if (response.error) {
        console.error(chalk.red(`Error: ${response.error.message}`));
        process.exit(1);
      }

      if (response.result?.isError) {
        const text = extractFirstText(response.result.content);
        console.error(chalk.red(text || "Unknown error"));
        process.exit(1);
      }

      const text = extractFirstText(response.result?.content ?? []);
      if (text) {
        console.log(text);
      }
    });
}