#!/usr/bin/env node

import { Command } from "commander";
import chalk from "chalk";
import { createClient } from "./client.js";
import { listToolsCommand } from "./commands/list-tools.js";
import { findUsagesCommand } from "./commands/find-usages.js";
import { findDefinitionCommand } from "./commands/find-definition.js";
import { findClassCommand } from "./commands/find-class.js";
import { findFileCommand } from "./commands/find-file.js";
import { searchTextCommand } from "./commands/search-text.js";
import { readFileCommand } from "./commands/read-file.js";
import { diagnosticsCommand } from "./commands/diagnostics.js";
import { indexStatusCommand } from "./commands/index-status.js";
import { syncFilesCommand } from "./commands/sync-files.js";
import { buildProjectCommand } from "./commands/build-project.js";
import { renameCommand } from "./commands/rename.js";
import { moveFileCommand } from "./commands/move-file.js";
import { reformatCodeCommand } from "./commands/reformat-code.js";
import { optimizeImportsCommand } from "./commands/optimize-imports.js";
import { safeDeleteCommand } from "./commands/safe-delete.js";
import { typeHierarchyCommand } from "./commands/type-hierarchy.js";
import { callHierarchyCommand } from "./commands/call-hierarchy.js";
import { findImplementationsCommand } from "./commands/find-implementations.js";
import { findSymbolCommand } from "./commands/find-symbol.js";
import { findSuperMethodsCommand } from "./commands/find-super-methods.js";
import { fileStructureCommand } from "./commands/file-structure.js";
import { convertJavaToKotlinCommand } from "./commands/convert-java-to-kotlin.js";
import { getActiveFileCommand } from "./commands/get-active-file.js";
import { openFileCommand } from "./commands/open-file.js";

const program = new Command();

program
  .name("jetbrains-cli")
  .description("CLI tool for JetBrains IDE code intelligence")
  .version("1.0.0")
  .option("--host <host>", "Server host", "127.0.0.1")
  .option("--port <port>", "Server port", "29170")
  .option("--json", "Output raw JSON", false);

// Register commands
listToolsCommand(program);
findUsagesCommand(program);
findDefinitionCommand(program);
findClassCommand(program);
findFileCommand(program);
searchTextCommand(program);
readFileCommand(program);
diagnosticsCommand(program);
indexStatusCommand(program);
syncFilesCommand(program);
buildProjectCommand(program);
renameCommand(program);
moveFileCommand(program);
reformatCodeCommand(program);
optimizeImportsCommand(program);
safeDeleteCommand(program);
typeHierarchyCommand(program);
callHierarchyCommand(program);
findImplementationsCommand(program);
findSymbolCommand(program);
findSuperMethodsCommand(program);
fileStructureCommand(program);
convertJavaToKotlinCommand(program);
getActiveFileCommand(program);
openFileCommand(program);

// Global error handler for connection issues
program.hook("preAction", async (_, actionCommand) => {
  const opts = program.opts();
  try {
    await createClient({ host: opts.host, port: parseInt(opts.port) });
  } catch (error) {
    if (error instanceof Error) {
      console.error(chalk.red(error.message));
      process.exit(1);
    }
  }
});

program.parse();