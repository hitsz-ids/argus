package io.ids.argus.core.conf.exception;

import java.io.Serializable;

public interface IError extends Serializable {
    String getMsg();

    int getCode();

    default IError convert(int code) {
        return this;
    }
}
