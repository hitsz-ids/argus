package io.ids.argus.store.server.exception;


import io.ids.argus.core.conf.exception.ArgusException;
import io.ids.argus.store.server.exception.error.ConfigurationError;

/**
 * Argus Store Configuration Exception
 */
public class ArgusConfigurationException extends ArgusException {

    public ArgusConfigurationException(ConfigurationError status) {
        super(status);
    }

}
