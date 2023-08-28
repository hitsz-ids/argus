package io.ids.argus.center.exception;

import io.ids.argus.center.common.ExecuteStatus;
import io.ids.argus.core.exception.ArgusException;

public class ArgusExecuteException extends ArgusException {
    public ArgusExecuteException(ExecuteStatus status) {
        super(status);
    }
}
