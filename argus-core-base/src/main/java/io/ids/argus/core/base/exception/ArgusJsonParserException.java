package io.ids.argus.core.base.exception;

import io.ids.argus.core.base.common.IStatus;

public class ArgusJsonParserException extends ArgusException {
    public ArgusJsonParserException(IStatus status) {
        super(status);
    }
}
