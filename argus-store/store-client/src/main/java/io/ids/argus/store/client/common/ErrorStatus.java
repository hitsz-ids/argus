package io.ids.argus.store.client.common;

import io.ids.argus.core.base.common.IStatus;

public enum ErrorStatus implements IStatus {
    TIME_OUT(80000, "执行超时"),
    ERROR(80001, "未知异常错误")
    ;
    private final String msg;
    private final int code;

    ErrorStatus(int code, String msg) {
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
