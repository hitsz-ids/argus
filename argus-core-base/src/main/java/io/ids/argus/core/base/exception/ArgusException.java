package io.ids.argus.core.base.exception;

import io.ids.argus.core.base.common.IStatus;

import java.util.Objects;

public abstract class ArgusException extends RuntimeException {
    protected final IStatus status;
    protected final String msg;
    public ArgusException(IStatus status) {
        super(String.format("%s: %d, %s", status.getClass().getSimpleName(),
                status.getCode(),
                status.getMsg()));
        this.status = status;
        msg = this.status.getMsg();
    }

    public ArgusException(IStatus status, String message, Object... args) {
        super(String.format("%s: %d, %s", status.getClass().getSimpleName(),
                status.getCode(), String.format(message, args)));
        this.status = status;
        this.msg = super.getMessage();
    }

    public int getCode() {
        return status.getCode();
    }

    public String getMsg() {
        return msg;
    }

    public <S extends IStatus> S getStatus(Class<S> clazz) {
        if (Objects.equals(status.getClass(), clazz)) {
            return (S) status;
        }
        throw new RuntimeException("illegal status");
    }
}
