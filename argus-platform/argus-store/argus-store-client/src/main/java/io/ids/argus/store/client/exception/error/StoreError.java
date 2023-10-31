package io.ids.argus.store.client.exception.error;


import io.ids.argus.core.conf.exception.IError;

/**
 * Argus core store error code
 */
public enum StoreError implements IError {
    TIME_OUT            (80000, "Execution timeout."),
    ERROR               (80001, "Unknown error."),
    ERROR_SESSION_ID    (80002, "Failed to obtain sessionId."),
    ERROR_CREATE        (80003, "Failed to create session.")
    ;
    private final String msg;
    private final int code;

    StoreError(int code, String msg) {
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
