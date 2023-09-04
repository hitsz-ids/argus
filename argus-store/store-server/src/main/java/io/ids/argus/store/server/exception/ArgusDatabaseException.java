package io.ids.argus.store.server.exception;

import io.ids.argus.core.base.exception.ArgusException;
import io.ids.argus.store.server.common.DatabaseStatus;

public class ArgusDatabaseException extends ArgusException {
    public ArgusDatabaseException(DatabaseStatus status) {
        super(status);
    }
}
