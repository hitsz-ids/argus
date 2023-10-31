package io.ids.argus.server.base.exception.error;


import io.ids.argus.core.conf.exception.IError;

/**
 * The Argus Server Network error code
 */
public enum NetworkError implements IError {

    UNAUTHENTICATED         (11000, "Module is not authorized."),
    NOT_FOUND               (11001, "Corresponding module configuration not found."),
    ALREADY_EXISTS          (11002, "The module has already been logged in, please do not log in again."),
    NOT_FOUND_COMMAND       (11003, "The corresponding request id was not found."),
    REQUEST_IS_END          (11004, "Request already ended.");

    private final int code;
    private final String msg;

    NetworkError(int code, String msg) {
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
