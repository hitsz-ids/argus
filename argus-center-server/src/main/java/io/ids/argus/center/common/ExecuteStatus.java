package io.ids.argus.center.common;

import io.ids.argus.core.base.common.IStatus;

public enum ExecuteStatus implements IStatus {
    EXECUTE_COMMAND_TIME_OUT(10000, "命令发送超时"),
    EXECUTE_COMMAND_FAILED(10001, "命令发送失败"),
    EXECUTE_INTERRUPTED(10002, "请求被中断")
    ;
    private final int code;
    private final String msg;

    ExecuteStatus(int code, String msg) {
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
