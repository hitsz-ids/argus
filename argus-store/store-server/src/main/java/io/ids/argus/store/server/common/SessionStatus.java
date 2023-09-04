package io.ids.argus.store.server.common;

import io.ids.argus.core.base.common.IStatus;

public enum SessionStatus implements IStatus {
    NOT_FOUND_SESSION(32000, "未找到对应session"),
            ;
    private final int code;
    private final String msg;

    SessionStatus(int code, String msg) {
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
