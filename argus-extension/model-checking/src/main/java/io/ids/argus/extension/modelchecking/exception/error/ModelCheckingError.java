package io.ids.argus.extension.modelchecking.exception.error;


import io.ids.argus.core.conf.exception.IError;

/**
 * ModelChecking error code
 */
public enum ModelCheckingError implements IError {
    ERROR_QUERY_STATUS               (1, "query status error."),
    ERROR_SCAN        (2, "Failed to scan.")
    ;
    private final String msg;
    private final int code;

    ModelCheckingError(int code, String msg) {
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
