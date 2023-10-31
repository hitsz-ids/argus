package io.ids.argus.core.base.exception.error;

import io.ids.argus.core.conf.exception.IError;

/**
 * Argus core invoke error code
 */
public enum InvokerError implements IError {
    NOT_FOUND_NAMESPACE         (90000, "Specified module not found."),
    NOT_FOUND_URL               (90001, "Specified URL not found."),
    NOT_FOUND_INVOKER           (90002, "Invocation method not found."),
    ERROR_INVOKE_RETURN         (90003, "The return value of the API interface must be the interface InvokerOutput."),
    ERROR_INVOKER_PARAMS        (90004, "Request parameters must be interface InvokerArgs."),
    ERROR_INVOKE                (90005, "Invoke error."),
    ERROR_PARSE_RETURN          (90006, "Failed to parse return value."),
    ERROR_INVOKE_JOB_RETURN     (90007, "The return value of the Job interface must be a subclass of JobEntity."),

    ;
    private final int code;
    private final String msg;

    InvokerError(int code, String msg) {
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
