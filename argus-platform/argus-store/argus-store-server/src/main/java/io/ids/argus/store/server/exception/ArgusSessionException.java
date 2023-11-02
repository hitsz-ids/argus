package io.ids.argus.store.server.exception;

import io.ids.argus.core.conf.exception.ArgusException;
import io.ids.argus.store.server.exception.error.SessionError;

/**
 * Argus Store Session Exception
 */
public class ArgusSessionException extends ArgusException {

    public ArgusSessionException(SessionError status) {
        super(status);
    }

}
