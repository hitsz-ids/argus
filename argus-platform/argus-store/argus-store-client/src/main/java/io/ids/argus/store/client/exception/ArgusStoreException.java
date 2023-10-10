package io.ids.argus.store.client.exception;

import io.ids.argus.core.conf.exception.ArgusException;
import io.ids.argus.store.client.exception.error.StoreError;

public class ArgusStoreException extends ArgusException {
    public ArgusStoreException(StoreError status) {
        super(status);
    }
}
