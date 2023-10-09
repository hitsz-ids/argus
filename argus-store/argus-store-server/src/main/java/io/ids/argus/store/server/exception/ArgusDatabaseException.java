package io.ids.argus.store.server.exception;

import io.ids.argus.core.conf.exception.ArgusException;
import io.ids.argus.store.server.exception.error.DatabaseError;

public class ArgusDatabaseException extends ArgusException {
    public ArgusDatabaseException(DatabaseError status) {
        super(status);
    }
}
