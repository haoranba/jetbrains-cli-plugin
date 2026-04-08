import { Command } from "commander";
import chalk from "chalk";
import { JetbrainsClient } from "../client.js";
import { extractFirstText } from "../utils.js";

export function moveFileCommand(program: Command) {
  program
    .command("move-file")
    .description("Move a file to a new directory")
    .requiredOption("--file <file>", "File path to move")
    .requiredOption("--new-directory <dir>", "Target directory")
    .option("--update-refs <bool>", "Update references (default: true)", "true")
    .action(async (options, cmd) => {
      const opts = cmd.parent.opts();
      const client = new JetbrainsClient({
        host: opts.host,
        port: parseInt(opts.port),
      });

      const args: Record<string, unknown> = {
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