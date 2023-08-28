package io.ids.argus.core.exception;

import io.ids.argus.core.common.IStatus;

public class ArgusJsonParserException extends ArgusException {
    public ArgusJsonParserException(IStatus status) {
        super(status);
    }
}
