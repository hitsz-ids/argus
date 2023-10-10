package io.ids.argus.store.client.exception.error;


import io.ids.argus.core.conf.exception.IError;

public enum StoreError implements IError {
    TIME_OUT(80000, "执行超时"),
    ERROR(80001, "未知异常错误"),
    ERROR_SESSION_ID(80002, "sessionId获取失败"),
    ERROR_CREATE(80003, "session创建失败")
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
