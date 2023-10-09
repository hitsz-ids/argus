package io.ids.argus.core.base.exception;

import io.ids.argus.core.base.exception.error.InvokerError;
import io.ids.argus.core.conf.exception.ArgusException;

public class ArgusInvokerException extends ArgusException {

    public ArgusInvokerException(InvokerError status) {
        super(status);
    }

    public ArgusInvokerException(InvokerError status, String message, Object... args) {
        super(status, message, args);
    }
}
