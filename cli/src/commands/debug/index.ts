import { Command } from "commander";
import { listConfigsCommand } from "./list-configs.js";
import { startSessionCommand } from "./start-session.js";
import { stopSessionCommand } from "./stop-session.js";
import { sessionStatusCommand } from "./session-status.js";
import { listSessionsCommand } from "./list-sessions.js";
import { setBreakpointCommand } from "./set-breakpoint.js";
import { listBreakpointsCommand } from "./list-breakpoints.js";
import { removeBreakpointCommand } from "./remove-breakpoint.js";
import { continueCommand } from "./continue.js";
import { pauseCommand } from "./pause.js";
import { stepOverCommand } from "./step-over.js";
import { stepIntoCommand } from "./step-into.js";
import { stepOutCommand } from "./step-out.js";
import { runToLineCommand } from "./run-to-line.js";
import { waitForPauseCommand } from "./wait-for-pause.js";
import { stackCommand } from "./stack.js";
import { threadsCommand } from "./threads.js";
import { selectFrameCommand } from "./select-frame.js";
import { variablesCommand } from "./variables.js";
import { setVariableCommand } from "./set-variable.js";
import { evaluateCommand } from "./evaluate.js";
import { sourceContextCommand } from "./source-context.js";

export function debugCommand(program: Command) {
  const debug = program
    .command("debug")
    .description("Debug commands for managing debug sessions and execution control");

  // Register all debug subcommands
  listConfigsCommand(debug);
  startSessionCommand(debug);
  stopSessionCommand(debug);
  sessionStatusCommand(debug);
  listSessionsCommand(debug);
  setBreakpointCommand(debug);
  listBreakpointsCommand(debug);
  removeBreakpointCommand(debug);
  continueCommand(debug);
  pauseCommand(debug);
  stepOverCommand(debug);
  stepIntoCommand(debug);
  stepOutCommand(debug);
  runToLineCommand(debug);
  waitForPauseCommand(debug);
  stackCommand(debug);
  threadsCommand(debug);
  selectFrameCommand(debug);
  variablesCommand(debug);
  setVariableCommand(debug);
  evaluateCommand(debug);
  sourceContextCommand(debug);
}