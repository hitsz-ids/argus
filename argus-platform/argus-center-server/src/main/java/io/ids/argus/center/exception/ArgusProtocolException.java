package io.ids.argus.center.exception;

import io.ids.argus.center.exception.error.ProtocolError;
import io.ids.argus.core.conf.exception.ArgusException;

/**
 * The Argus Protocol Exception
 * <p>
 * Throw when the Exception is
 */
public class ArgusProtocolException extends ArgusException {

    public ArgusProtocolException(ProtocolError status) {
        super(status);
    }

}
