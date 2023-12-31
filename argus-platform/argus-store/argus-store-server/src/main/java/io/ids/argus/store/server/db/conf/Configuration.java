package io.ids.argus.store.server.db.conf;

import io.ids.argus.store.server.exception.error.ConfigurationError;
import io.ids.argus.store.server.exception.ArgusConfigurationException;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * The Argus store layer configuration
 */
@Data
public class Configuration {

    protected String  host;
    protected Integer port;
    protected String  username;
    protected String  auth;
    protected String  database;
    protected String  resource;

    Configuration() {
        // Get properties from configuration files
        String host = DbProperties.get().getHost();
        if (StringUtils.isBlank(host)) {
            throw new ArgusConfigurationException(ConfigurationError.CONF_HOST_ERROR);
        }

        int port = DbProperties.get().getPort();

        String database = DbProperties.get().getDatabase();
        if (StringUtils.isBlank(database)) {
            throw new ArgusConfigurationException(ConfigurationError.CONF_DATABASE_ERROR);
        }

        String username = DbProperties.get().getUsername();
        if (StringUtils.isBlank(username)) {
            throw new ArgusConfigurationException(ConfigurationError.CONF_USERNAME_ERROR);
        }
        String auth = DbProperties.get().getAuth();
        if (StringUtils.isBlank(auth)) {
            throw new ArgusConfigurationException(ConfigurationError.CONF_AUTH_ERROR);
        }

        this.host = host;
        this.port = port;
        this.username = username;
        this.auth = auth;
        this.database = database;
        this.resource = "argus-db-config.xml";
    }
}
