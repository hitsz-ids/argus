package io.ids.argus.core.exception;

import io.ids.argus.core.common.ScanStatus;

import java.io.IOException;

public class ArgusScannerException extends ArgusException {

    public ArgusScannerException(ScanStatus status) {
        super(status);
    }
}
