package io.ids.argus.store.client.exception;

import io.ids.argus.core.conf.exception.ArgusException;
import io.ids.argus.store.client.exception.error.FileStoreError;
import io.ids.argus.store.client.exception.error.StoreError;

public class ArgusFileStoreException extends ArgusException {
    public ArgusFileStoreException(FileStoreError status) {
        super(status);
    }
}
