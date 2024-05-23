package io.ids.argus.store.client.exception;

import io.ids.argus.core.conf.exception.ArgusException;
import io.ids.argus.store.client.exception.error.FileStoreError;

public class ArgusFileStoreException extends ArgusException {
    public ArgusFileStoreException(FileStoreError status) {
        super(status);
    }
}
