package io.ids.argus.core.base.exception;

import io.ids.argus.core.base.common.ScanStatus;

public class ArgusScannerException extends ArgusException {

    public ArgusScannerException(ScanStatus status) {
        super(status);
    }
}
