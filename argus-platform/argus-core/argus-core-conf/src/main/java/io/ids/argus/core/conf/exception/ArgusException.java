package io.ids.argus.core.conf.exception;


import java.util.Objects;

/**
 * The parent class of all Argus exceptions
 */
public abstract class ArgusException extends RuntimeException {

    protected final IError status;
    protected final String msg;

    public ArgusException(IError status) {
        super(String.format("%s: %d, %s", status.getClass().getSimpleName(),
                status.getCode(),
                status.getMsg()));
        this.status = status;
        msg = getMessage();
    }

    public ArgusException(IError status, String message, Object... args) {
        super(String.format("%s: %d, %s", status.getClass().getSimpleName(),
                status.getCode(), String.format(message, args)));
        this.status = status;
        this.msg = getMessage();
    }

    public int getCode() {
        return status.getCode();
    }

    public String getMsg() {
        return msg;
    }

    public <S extends IError> S getStatus(Class<S> clazz) {
        if (Objects.equals(status.getClass(), clazz)) {
            return (S) status;
        }
        throw new RuntimeException("illegal status");
    }
}
