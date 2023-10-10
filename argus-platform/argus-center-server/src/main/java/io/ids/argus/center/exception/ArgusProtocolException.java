package io.ids.argus.center.exception;

import io.ids.argus.center.exception.error.ProtocolError;
import io.ids.argus.core.conf.exception.ArgusException;

public class ArgusProtocolException extends ArgusException {
    public ArgusProtocolException(ProtocolError status) {
        super(status);
    }
}
