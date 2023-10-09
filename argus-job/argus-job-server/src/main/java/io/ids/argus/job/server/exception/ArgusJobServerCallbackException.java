package io.ids.argus.job.server.exception;

import io.ids.argus.core.conf.exception.ArgusException;
import io.ids.argus.job.server.error.CallbackError;

public class ArgusJobServerCallbackException extends ArgusException {
    public ArgusJobServerCallbackException(CallbackError status) {
        super(status);
    }
}
