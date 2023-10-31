package io.ids.argus.job.server.error;

import io.ids.argus.core.conf.exception.IError;

/**
 * Argus Job Server error code
 */
public enum CallbackError implements IError {

    ERROR_CALLBACK              (70000, "Job message callback failed."),
    ERROR_CALLBACK_TIME_OUT     (70001, "Job message callback timeout."),
    ERROR_EXECUTE_JOB_TIME_OUT  (70002, "Task execution timeout."),
    ERROR_NOT_FOUND_JOB         (70003, "Job not found.")
    ;

    private final int code;
    private final String msg;

    CallbackError(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String getMsg() {
        return msg;
    }

    @Override
    public int getCode() {
        return code;
    }
}
