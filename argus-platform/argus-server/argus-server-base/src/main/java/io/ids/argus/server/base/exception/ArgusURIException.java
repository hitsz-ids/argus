package io.ids.argus.server.base.exception;

import io.ids.argus.core.conf.exception.ArgusException;
import io.ids.argus.server.base.exception.error.URIError;

public class ArgusURIException extends ArgusException {
    public ArgusURIException(URIError status) {
        super(status);
    }
}
