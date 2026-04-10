import { Command } from "commander";
import chalk from "chalk";
import { JetbrainsClient } from "../../client.js";
import { extractFirstText } from "../../utils.js";

export function setBreakpointCommand(program: Command) {
  program
    .command("set-breakpoint")
    .description("Set breakpoint")
    .requiredOption("--file <file>", "File path")
    .requiredOption("--line <line>", "Line number")
    .option("--condition <condition>", "Breakpoint condition (optional)")
    .option("--session <id>", "Session ID (optional, uses current session if not provided)")
    .action(async (options, cmd) => {
      const opts = cmd.parent.parent?.opts() || cmd.parent.opts();
      const client = new JetbrainsClient({
        host: opts.host,
        port: parseInt(opts.port),
      });

      const params: Record<string, unknown> = {
        file: options.file,
        line: parseInt(options.line),
      };

      if (options.session) {
        params.sessionId = options.session;
      }
      if (options.condition) {
        params.condition = options.condition;
      }

      const response = await client.callTool("ide_set_breakpoint", params);

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