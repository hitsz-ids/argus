package io.ids.argus.store.server.exception;

import io.ids.argus.core.base.exception.ArgusException;
import io.ids.argus.store.server.common.SessionStatus;

public class ArgusSessionException extends ArgusException {
    public ArgusSessionException(SessionStatus status) {
        super(status);
    }
}
