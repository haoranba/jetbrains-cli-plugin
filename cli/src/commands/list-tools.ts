import { Command } from "commander";
import chalk from "chalk";
import { JetbrainsClient } from "../client.js";

export function listToolsCommand(program: Command) {
  program
    .command("list-tools")
    .description("List all available tools")
    .action(async (_, cmd) => {
      const opts = cmd.parent.opts();
      const client = new JetbrainsClient({
        host: opts.host,
        port: parseInt(opts.port),
      });

      const result = await client.listTools();

      if (opts.json) {
        console.log(JSON.stringify(result, null, 2));
        return;
      }

      console.log(chalk.bold(`Available tools (${result.tools.length}):`));
      console.log();

      for (const tool of result.tools) {
        console.log(chalk.cyan(tool.name));
        console.log(`  ${tool.description}`);
        console.log();
      }
    });
}