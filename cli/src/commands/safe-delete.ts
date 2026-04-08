import { Command } from "commander";
import chalk from "chalk";
import { JetbrainsClient } from "../client.js";
import { extractFirstText } from "../utils.js";

export function safeDeleteCommand(program: Command) {
  program
    .command("safe-delete")
    .description("Safely delete an element (Java/Kotlin only)")
    .option("--file <file>", "File path")
    .option("--line <line>", "Line number (1-based)")
    .option("--column <column>", "Column number (1-based)")
    .action(async (options, cmd) => {
      const opts = cmd.parent.opts();
      const client = new JetbrainsClient({
        host: opts.host,
        port: parseInt(opts.port),
      });

      const args: Record<string, unknown> = {};

      if (options.file) args.file = options.file;
      if (options.line) args.line = parseInt(options.line);
      if (options.column) args.column = parseInt(options.column);

      const response = await client.callTool("ide_refactor_safe_delete", args);

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
        console.log(chalk.green(text));
      }
    });
}