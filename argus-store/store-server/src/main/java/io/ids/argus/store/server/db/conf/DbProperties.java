package io.ids.argus.store.server.db.conf;

import io.ids.argus.core.base.conf.ArgusProperties;

public class DbProperties extends ArgusProperties {
    private static final String PROPERTIES_PATH = "argus-db.properties";

    private static final DbProperties instance = new DbProperties();

    private DbProperties() {
    }

    public static DbProperties get() {
        return instance;
    }

    @Override
    public String initPath() {
        return PROPERTIES_PATH;
    }

    public String getHost() {
        return getString("argus.store.db.host");
    }

    public int getPort() {
        return getInt("argus.store.db.port");
    }

    public String getUsername() {
        return getString("argus.store.db.username");
    }

    public String getAuth() {
        return getString("argus.store.db.auth");
    }

    public String getDatabase() {
        return getString("argus.store.db.database");
    }
}
