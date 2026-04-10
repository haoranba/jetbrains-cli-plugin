import { Command } from "commander";
import chalk from "chalk";
import { JetbrainsClient } from "../../client.js";
import { extractFirstText } from "../../utils.js";

export function startSessionCommand(program: Command) {
  program
    .command("start-session")
    .description("Start debug session")
    .requiredOption("--config <name>", "Run configuration name")
    .option("--env <env>", "Environment variables (JSON string)")
    .action(async (options, cmd) => {
      const opts = cmd.parent.parent?.opts() || cmd.parent.opts();
      const client = new JetbrainsClient({
        host: opts.host,
        port: parseInt(opts.port),
      });

      const params: Record<string, unknown> = {
        configurationName: options.config,
      };

      if (options.env) {
        params.environmentVariables = options.env;
      }

      const response = await client.callTool("ide_start_debug_session", params);

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