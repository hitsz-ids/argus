package io.ids.argus.store.server.exception.error;


import io.ids.argus.core.conf.exception.IError;

/**
 * Argus Store Database error code
 */
public enum DatabaseError implements IError {
    DATABASE_SESSION_GET_MAPPER_ERROR (31001, "Failed to obtain mapper of mybatis."),
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
