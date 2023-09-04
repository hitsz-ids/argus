package io.ids.argus.store.server.common;

import io.ids.argus.core.base.common.IStatus;

public enum DatabaseStatus implements IStatus {
    DATABASE_SESSION_GET_MAPPER_ERROR(31001, "获取mybatis的mapper失败"),
    ;
    private final int code;
    private final String msg;

    DatabaseStatus(int code, String msg) {
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
