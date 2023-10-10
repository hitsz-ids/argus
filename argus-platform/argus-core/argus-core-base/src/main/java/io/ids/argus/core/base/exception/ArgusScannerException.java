package io.ids.argus.core.base.exception;

import io.ids.argus.core.base.exception.error.ScanError;
import io.ids.argus.core.conf.exception.ArgusException;

public class ArgusScannerException extends ArgusException {

    public ArgusScannerException(ScanError status) {
        super(status);
    }
}
