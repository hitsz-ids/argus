package io.ids.argus.server.base.exception.error;


import io.ids.argus.core.conf.exception.IError;

public enum ModuleError implements IError {
    ERROR_REQUEST_TIME_OUT(13000, "请求发送超时失败"),
    ERROR_REQUEST_INTERCEPTED(13001, "请求异常中断");
    private final int code;
    private final String msg;

    ModuleError(int code, String msg) {
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
