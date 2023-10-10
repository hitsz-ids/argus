package io.ids.argus.store.server.exception.error;


import io.ids.argus.core.conf.exception.IError;

public enum SessionError implements IError {
    NOT_FOUND_SESSION(32000, "未找到对应session"),
            ;
    private final int code;
    private final String msg;

    SessionError(int code, String msg) {
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
