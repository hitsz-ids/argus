package io.ids.argus.center.exception;

import io.ids.argus.center.common.NetworkStatus;
import io.ids.argus.core.base.exception.ArgusException;

public class ArgusInterceptException extends ArgusException {
    public ArgusInterceptException(NetworkStatus status) {
        super(status);
    }

}