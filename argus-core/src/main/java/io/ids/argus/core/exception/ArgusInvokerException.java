package io.ids.argus.core.exception;

import io.ids.argus.core.common.IStatus;
import io.ids.argus.core.common.InvokerStatus;

public class ArgusInvokerException extends ArgusException {

    public ArgusInvokerException(InvokerStatus status) {
        super(status);
    }

    public ArgusInvokerException(IStatus status, String message, Object... args) {
        super(status, message, args);
    }
}
