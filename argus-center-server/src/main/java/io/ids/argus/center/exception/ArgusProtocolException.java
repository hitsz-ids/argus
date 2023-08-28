package io.ids.argus.center.exception;

import io.ids.argus.center.common.ParserStatus;
import io.ids.argus.core.exception.ArgusException;

public class ArgusProtocolException extends ArgusException {
    public ArgusProtocolException(ParserStatus status) {
        super(status);
    }
}
