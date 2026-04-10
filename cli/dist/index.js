#!/usr/bin/env node
"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const commander_1 = require("commander");
const chalk_1 = __importDefault(require("chalk"));
const client_js_1 = require("./client.js");
const list_tools_js_1 = require("./commands/list-tools.js");
const find_usages_js_1 = require("./commands/find-usages.js");
const find_definition_js_1 = require("./commands/find-definition.js");
const find_class_js_1 = require("./commands/find-class.js");
const find_file_js_1 = require("./commands/find-file.js");
const search_text_js_1 = require("./commands/search-text.js");
const read_file_js_1 = require("./commands/read-file.js");
const diagnostics_js_1 = require("./commands/diagnostics.js");
const index_status_js_1 = require("./commands/index-status.js");
const sync_files_js_1 = require("./commands/sync-files.js");
const build_project_js_1 = require("./commands/build-project.js");
const rename_js_1 = require("./commands/rename.js");
const move_file_js_1 = require("./commands/move-file.js");
const reformat_code_js_1 = require("./commands/reformat-code.js");
const optimize_imports_js_1 = require("./commands/optimize-imports.js");
const safe_delete_js_1 = require("./commands/safe-delete.js");
const type_hierarchy_js_1 = require("./commands/type-hierarchy.js");
const call_hierarchy_js_1 = require("./commands/call-hierarchy.js");
const find_implementations_js_1 = require("./commands/find-implementations.js");
const find_symbol_js_1 = require("./commands/find-symbol.js");
const find_super_methods_js_1 = require("./commands/find-super-methods.js");
const file_structure_js_1 = require("./commands/file-structure.js");
const convert_java_to_kotlin_js_1 = require("./commands/convert-java-to-kotlin.js");
const get_active_file_js_1 = require("./commands/get-active-file.js");
const open_file_js_1 = require("./commands/open-file.js");
const index_js_1 = require("./commands/debug/index.js");
const program = new commander_1.Command();
program
    .name("jetbrains-cli")
    .description("CLI tool for JetBrains IDE code intelligence")
    .version("1.0.0")
    .option("--host <host>", "Server host", "127.0.0.1")
    .option("--port <port>", "Server port", "29170")
    .option("--project-path <path>", "Project path (required when multiple projects are open)")
    .option("--json", "Output raw JSON", false);
// Register commands
(0, list_tools_js_1.listToolsCommand)(program);
(0, find_usages_js_1.findUsagesCommand)(program);
(0, find_definition_js_1.findDefinitionCommand)(program);
(0, find_class_js_1.findClassCommand)(program);
(0, find_file_js_1.findFileCommand)(program);
(0, search_text_js_1.searchTextCommand)(program);
(0, read_file_js_1.readFileCommand)(program);
(0, diagnostics_js_1.diagnosticsCommand)(program);
(0, index_status_js_1.indexStatusCommand)(program);
(0, sync_files_js_1.syncFilesCommand)(program);
(0, build_project_js_1.buildProjectCommand)(program);
(0, rename_js_1.renameCommand)(program);
(0, move_file_js_1.moveFileCommand)(program);
(0, reformat_code_js_1.reformatCodeCommand)(program);
(0, optimize_imports_js_1.optimizeImportsCommand)(program);
(0, safe_delete_js_1.safeDeleteCommand)(program);
(0, type_hierarchy_js_1.typeHierarchyCommand)(program);
(0, call_hierarchy_js_1.callHierarchyCommand)(program);
(0, find_implementations_js_1.findImplementationsCommand)(program);
(0, find_symbol_js_1.findSymbolCommand)(program);
(0, find_super_methods_js_1.findSuperMethodsCommand)(program);
(0, file_structure_js_1.fileStructureCommand)(program);
(0, convert_java_to_kotlin_js_1.convertJavaToKotlinCommand)(program);
(0, get_active_file_js_1.getActiveFileCommand)(program);
(0, open_file_js_1.openFileCommand)(program);
(0, index_js_1.debugCommand)(program);
// Global error handler for connection issues
program.hook("preAction", async (_, actionCommand) => {
    const opts = program.opts();
    try {
        await (0, client_js_1.createClient)({ host: opts.host, port: parseInt(opts.port) });
    }
    catch (error) {
        if (error instanceof Error) {
            console.error(chalk_1.default.red(error.message));
            process.exit(1);
        }
    }
});
program.parse();
//# sourceMappingURL=index.js.map