package io.ids.argus.extension.modelchecking.exception;

import io.ids.argus.core.conf.exception.ArgusException;
import io.ids.argus.extension.modelchecking.exception.error.ModelCheckingError;

public class ModelCheckingException extends ArgusException {
    public ModelCheckingException(ModelCheckingError status) {
        super(status);
    }
}
