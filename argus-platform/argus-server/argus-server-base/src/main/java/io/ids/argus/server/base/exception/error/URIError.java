package io.ids.argus.server.base.exception.error;


import io.ids.argus.core.conf.exception.IError;

/**
 * Argus URI parse error code
 */
public enum URIError implements IError {

    ERROR_PARSE_MODULE(10000, "Failed to parse module configurations."),
    ;

    private final int code;
    private final String msg;

    URIError(int code, String msg) {
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
