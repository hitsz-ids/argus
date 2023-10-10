package io.ids.argus.server.base.exception;

import io.ids.argus.core.conf.exception.ArgusException;
import io.ids.argus.server.base.exception.error.ProtocolError;

public class ArgusProtocolException extends ArgusException {
    public ArgusProtocolException(ProtocolError status) {
        super(status);
    }
}
