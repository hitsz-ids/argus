package io.ids.argus.core.base.exception;

import io.ids.argus.core.base.common.IStatus;
import io.ids.argus.core.base.common.InvokerStatus;

public class ArgusInvokerException extends ArgusException {

    public ArgusInvokerException(InvokerStatus status) {
        super(status);
    }

    public ArgusInvokerException(IStatus status, String message, Object... args) {
        super(status, message, args);
    }
}
