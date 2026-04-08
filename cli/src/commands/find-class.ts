import { Command } from "commander";
import chalk from "chalk";
import { JetbrainsClient } from "../client.js";
import { extractFirstText } from "../utils.js";

export function findClassCommand(program: Command) {
  program
    .command("find-class")
    .description("Search for classes/interfaces by name")
    .requiredOption("--query <query>", "Class name to search for")
    .option("--match-mode <mode>", "Match mode: substring, prefix, exact", "substring")
    .option("--max-results <n>", "Maximum results to return", "100")
    .action(async (options, cmd) => {
      const opts = cmd.parent.opts();
      const client = new JetbrainsClient({
        host: opts.host,
        port: parseInt(opts.port),
      });

      const args: Record<string, unknown> = {
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

function printResult(response: any) {
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
}