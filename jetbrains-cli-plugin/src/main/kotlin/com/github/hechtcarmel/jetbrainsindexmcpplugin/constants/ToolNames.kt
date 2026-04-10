package com.github.hechtcarmel.jetbrainsindexmcpplugin.constants

object ToolNames {
    // Navigation tools
    const val FIND_REFERENCES = "ide_find_references"
    const val FIND_DEFINITION = "ide_find_definition"
    const val TYPE_HIERARCHY = "ide_type_hierarchy"
    const val CALL_HIERARCHY = "ide_call_hierarchy"
    const val FIND_IMPLEMENTATIONS = "ide_find_implementations"
    const val FIND_SYMBOL = "ide_find_symbol"
    const val FIND_SUPER_METHODS = "ide_find_super_methods"
    const val FILE_STRUCTURE = "ide_file_structure"
    const val FIND_CLASS = "ide_find_class"
    const val FIND_FILE = "ide_find_file"
    const val SEARCH_TEXT = "ide_search_text"
    const val READ_FILE = "ide_read_file"

    // Intelligence tools
    const val DIAGNOSTICS = "ide_diagnostics"

    // Project tools
    const val INDEX_STATUS = "ide_index_status"
    const val SYNC_FILES = "ide_sync_files"
    const val BUILD_PROJECT = "ide_build_project"

    // Refactoring tools
    const val REFACTOR_RENAME = "ide_refactor_rename"
    const val REFACTOR_SAFE_DELETE = "ide_refactor_safe_delete"
    const val REFACTOR_MOVE = "ide_move_file"
    const val REFORMAT_CODE = "ide_reformat_code"
    const val OPTIMIZE_IMPORTS = "ide_optimize_imports"
    const val CONVERT_JAVA_TO_KOTLIN = "ide_convert_java_to_kotlin"

    // Editor tools
    const val GET_ACTIVE_FILE = "ide_get_active_file"
    const val OPEN_FILE = "ide_open_file"

    // Debugger tools
    const val LIST_RUN_CONFIGURATIONS = "ide_list_run_configurations"
    const val EXECUTE_RUN_CONFIGURATION = "ide_execute_run_configuration"
    const val LIST_DEBUG_SESSIONS = "ide_list_debug_sessions"
    const val START_DEBUG_SESSION = "ide_start_debug_session"
    const val STOP_DEBUG_SESSION = "ide_stop_debug_session"
    const val GET_DEBUG_SESSION_STATUS = "ide_get_debug_session_status"
    const val LIST_BREAKPOINTS = "ide_list_breakpoints"
    const val SET_BREAKPOINT = "ide_set_breakpoint"
    const val REMOVE_BREAKPOINT = "ide_remove_breakpoint"
    const val RESUME_EXECUTION = "ide_resume_execution"
    const val PAUSE_EXECUTION = "ide_pause_execution"
    const val STEP_OVER = "ide_step_over"
    const val STEP_INTO = "ide_step_into"
    const val STEP_OUT = "ide_step_out"
    const val RUN_TO_LINE = "ide_run_to_line"
    const val WAIT_FOR_PAUSE = "ide_wait_for_pause"
    const val GET_STACK_TRACE = "ide_get_stack_trace"
    const val SELECT_STACK_FRAME = "ide_select_stack_frame"
    const val LIST_THREADS = "ide_list_threads"
    const val GET_VARIABLES = "ide_get_variables"
    const val SET_VARIABLE = "ide_set_variable"
    const val GET_SOURCE_CONTEXT = "ide_get_source_context"
    const val EVALUATE_EXPRESSION = "ide_evaluate_expression"

    /**
     * All known tool names, sorted alphabetically.
     * Keep this list in sync when adding or removing tool name constants.
     */
    val ALL: List<String> = listOf(
        BUILD_PROJECT,
        CALL_HIERARCHY,
        CONVERT_JAVA_TO_KOTLIN,
        DIAGNOSTICS,
        EVALUATE_EXPRESSION,
        EXECUTE_RUN_CONFIGURATION,
        FILE_STRUCTURE,
        FIND_CLASS,
        FIND_DEFINITION,
        FIND_FILE,
        FIND_IMPLEMENTATIONS,
        FIND_REFERENCES,
        FIND_SUPER_METHODS,
        FIND_SYMBOL,
        GET_ACTIVE_FILE,
        GET_DEBUG_SESSION_STATUS,
        GET_SOURCE_CONTEXT,
        GET_STACK_TRACE,
        GET_VARIABLES,
        INDEX_STATUS,
        LIST_BREAKPOINTS,
        LIST_DEBUG_SESSIONS,
        LIST_RUN_CONFIGURATIONS,
        LIST_THREADS,
        REFACTOR_MOVE,
        OPEN_FILE,
        OPTIMIZE_IMPORTS,
        PAUSE_EXECUTION,
        READ_FILE,
        REFACTOR_RENAME,
        REFACTOR_SAFE_DELETE,
        REFORMAT_CODE,
        REMOVE_BREAKPOINT,
        RESUME_EXECUTION,
        RUN_TO_LINE,
        SEARCH_TEXT,
        SELECT_STACK_FRAME,
        SET_BREAKPOINT,
        SET_VARIABLE,
        START_DEBUG_SESSION,
        STEP_INTO,
        STEP_OUT,
        STEP_OVER,
        STOP_DEBUG_SESSION,
        SYNC_FILES,
        TYPE_HIERARCHY,
        WAIT_FOR_PAUSE
    )
}
