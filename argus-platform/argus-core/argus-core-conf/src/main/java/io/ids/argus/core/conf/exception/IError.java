package io.ids.argus.core.conf.exception;

import java.io.Serializable;

/**
 * Error Code Interface
 * <p>
 * The parent interface of all the error code in the argus project.
 */
public interface IError extends Serializable {

    String getMsg();

    int getCode();

    default IError convert(int code) {
        return this;
    }

}
