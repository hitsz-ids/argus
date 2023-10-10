package io.ids.argus.server.base.exception;

import io.ids.argus.core.conf.exception.ArgusException;
import io.ids.argus.server.base.exception.error.NetworkError;

public class ArgusInterceptException extends ArgusException {
    public ArgusInterceptException(NetworkError status) {
        super(status);
    }

}