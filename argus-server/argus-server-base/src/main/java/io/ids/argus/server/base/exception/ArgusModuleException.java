package io.ids.argus.server.base.exception;

import io.ids.argus.core.conf.exception.ArgusException;
import io.ids.argus.server.base.exception.error.ModuleError;

public class ArgusModuleException extends ArgusException {
    public ArgusModuleException(ModuleError status) {
        super(status);
    }
}
