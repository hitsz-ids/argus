package io.ids.argus.core.base.exception;

import io.ids.argus.core.conf.exception.ArgusException;
import io.ids.argus.core.conf.exception.IError;

public class ArgusJsonParserException extends ArgusException {
    public ArgusJsonParserException(IError status) {
        super(status);
    }
}
