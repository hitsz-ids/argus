package io.ids.argus.store.server.exception;

import io.ids.argus.core.conf.exception.ArgusException;
import io.ids.argus.store.server.exception.error.DatabaseError;
import io.ids.argus.store.server.exception.error.FileError;

/**
 * Argus Store File Exception
 */
public class ArgusFileException extends ArgusException {

    public ArgusFileException(FileError status) {
        super(status);
    }

}
