import { Command } from "commander";
import chalk from "chalk";
import { JetbrainsClient } from "../client.js";
import { extractFirstText } from "../utils.js";

export function openFileCommand(program: Command) {
  program
    .command("open-file")
    .description("Open a file in the editor")
    .requiredOption("--file <file>", "File path to open")
    .option("--line <line>", "Line number to navigate to")
    .option("--column <column>", "Column number to navigate to")
    .action(async (options, cmd) => {
      const opts = cmd.parent.opts();
      const client = new JetbrainsClient({
        host: opts.host,
        port: parseInt(opts.port),
      });

      const args: Record<string, unknown> = {
        file: options.file,
      };

      if (options.line) args.line = parseInt(options.line);
      if (options.column) args.column = parseInt(options.column);

      const response = await client.callTool("ide_open_file", args);

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