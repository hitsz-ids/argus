package io.ids.argus.center.exception.error;


import io.ids.argus.core.conf.exception.IError;

/**
 * Protocol Error Code
 * <p>
 * All protocol error code list here. Start from code 12000.
 */
public enum ProtocolError implements IError {

    MODULE_NOT_FOUND        (12000, "Field [module] not found, please check the params!"),
    VERSION_NOT_FOUND       (12001, "Field [version] not found, please check the params!"),
    NAMESPACE_NOT_FOUND     (12002, "Field [namespace] not found, please check the params!"),
    URL_NOT_FOUND           (12003, "Field [url] not found, please check the params!"),
    ADDRESS_IS_NULL         (12004, "Field [address] is empty, please check the params!"),
    URI_ERROR               (12005, "Failed to parse URI, please check the params!"),
    ;

    private final int code;
    private final String msg;

    ProtocolError(int code, String msg) {
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
