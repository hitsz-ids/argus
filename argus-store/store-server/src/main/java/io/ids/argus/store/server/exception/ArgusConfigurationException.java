package io.ids.argus.store.server.exception;


import io.ids.argus.core.base.exception.ArgusException;
import io.ids.argus.store.server.common.ConfigurationStatus;

public class ArgusConfigurationException extends ArgusException {
    public ArgusConfigurationException(ConfigurationStatus status) {
        super(status);
    }
}
