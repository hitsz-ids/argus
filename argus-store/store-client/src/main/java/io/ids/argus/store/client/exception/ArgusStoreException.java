package io.ids.argus.store.client.exception;

import io.ids.argus.core.base.common.IStatus;
import io.ids.argus.core.base.exception.ArgusException;
import io.ids.argus.store.client.common.ErrorStatus;

public class ArgusStoreException extends ArgusException {
    public ArgusStoreException(ErrorStatus status) {
        super(status);
    }
}
