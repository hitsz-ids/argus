package io.ids.argus.job.server.error;

import io.ids.argus.core.conf.exception.IError;

public enum CallbackError implements IError {
    ERROR_CALLBACK(70000, "消息回复失败"),
    ERROR_CALLBACK_TIME_OUT(70001, "消息回复超时"),
    ERROR_EXECUTE_JOB_TIME_OUT(70002, "任务执行超时"),
    ERROR_NOT_FOUND_JOB(70003, "任务不存在")
    ;
    private final int code;
    private final String msg;

    CallbackError(int code, String msg) {
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
