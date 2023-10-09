package io.ids.argus.job.client.error;

import io.ids.argus.core.conf.exception.IError;

public enum JobError implements IError {
    ERROR_PRE_INIT(50000, "ArgusJob还未初始化，请先调用init方法"),
    ERROR_INIT(50001, "ArgusJob创建失败")
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
