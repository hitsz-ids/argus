package io.ids.argus.job.client.error;

import io.ids.argus.core.conf.exception.IError;

/**
 * Argus Job error code
 */
public enum JobError implements IError {

    ERROR_PRE_INIT  (50000, "ArgusJob has not been initialized yet, please call the init method first."),
    ERROR_INIT      (50001, "Failed to create ArgusJob.")
    ;

    private final int code;
    private final String msg;

    JobError(int code, String msg) {
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
