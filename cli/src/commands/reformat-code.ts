import { Command } from "commander";
import chalk from "chalk";
import { JetbrainsClient } from "../client.js";
import { extractFirstText } from "../utils.js";

export function reformatCodeCommand(program: Command) {
  program
    .command("reformat-code")
    .description("Reformat code using project code style")
    .option("--file <file>", "File path (optional, uses active file if omitted)")
    .option("--optimize-imports", "Optimize imports after reformatting", false)
    .option("--rearange", "Rearrange code elements", false)
    .action(async (options, cmd) => {
      const opts = cmd.parent.opts();
      const client = new JetbrainsClient({
        host: opts.host,
        port: parseInt(opts.port),
      });

      const args: Record<string, unknown> = {
        optimizeImports: options.optimizeImports,
        rearrange: options.rearange,
      };

      if (options.file) args.file = options.file;

      const response = await client.callTool("ide_reformat_code", args);

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