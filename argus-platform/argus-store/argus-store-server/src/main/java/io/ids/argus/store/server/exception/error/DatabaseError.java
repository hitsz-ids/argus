package io.ids.argus.store.server.exception.error;


import io.ids.argus.core.conf.exception.IError;

public enum DatabaseError implements IError {
    DATABASE_SESSION_GET_MAPPER_ERROR(31001, "获取mybatis的mapper失败"),
    ;
    private final int code;
    private final String msg;

    DatabaseError(int code, String msg) {
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
