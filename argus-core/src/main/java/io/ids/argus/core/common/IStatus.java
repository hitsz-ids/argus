package io.ids.argus.core.common;

import java.io.Serializable;

public interface IStatus extends Serializable {
    String getMsg();

    int getCode();

    default IStatus convert(int code) {
        return this;
    }
}
