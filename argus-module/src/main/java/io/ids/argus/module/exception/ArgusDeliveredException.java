package io.ids.argus.module.exception;

import io.ids.argus.core.conf.exception.ArgusException;
import io.ids.argus.core.conf.exception.IError;

public class ArgusDeliveredException extends ArgusException {
    public ArgusDeliveredException(IError status) {
        super(status);
    }
}
