import { Command } from "commander";
import chalk from "chalk";
import { JetbrainsClient } from "../client.js";
import { extractFirstText } from "../utils.js";

export function findUsagesCommand(program: Command) {
  program
    .command("find-usages")
    .description("Find all usages of a symbol")
    .option("--file <file>", "File path")
    .option("--line <line>", "Line number (1-based)")
    .option("--column <column>", "Column number (1-based)")
    .option("--language <language>", "Language filter")
    .option("--symbol <symbol>", "Symbol name (alternative to file/line/column)")
    .option("--max-results <n>", "Maximum results to return", "100")
    .action(async (options, cmd) => {
      const opts = cmd.parent.opts();
      const client = new JetbrainsClient({
        host: opts.host,
        port: parseInt(opts.port),
      });

      const args: Record<string, unknown> = {
        maxResults: parseInt(options.maxResults),
      };

      if (options.file) args.file = options.file;
      if (options.line) args.line = parseInt(options.line);
      if (options.column) args.column = parseInt(options.column);
      if (options.language) args.language = options.language;
      if (options.symbol) args.symbol = options.symbol;

      const response = await client.callTool("ide_find_references", args);

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