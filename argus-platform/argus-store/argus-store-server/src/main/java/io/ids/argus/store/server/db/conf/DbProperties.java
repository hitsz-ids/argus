package io.ids.argus.store.server.db.conf;


import io.ids.argus.core.conf.propertie.ArgusProperties;

/**
 * Database properties of Argus Store
 */
public class DbProperties extends ArgusProperties {
    private static final DbProperties instance = new DbProperties();

    private static final String PROPERTIES_PATH = "argus-db.properties";

    private static final String STORE_DB_HOST     = "argus.store.db.host";
    private static final String STORE_DB_PORT     = "argus.store.db.port";
    private static final String STORE_DB_USERNAME = "argus.store.db.username";
    private static final String STORE_DB_AUTH     = "argus.store.db.auth";
    private static final String STORE_DB_DATABASE = "argus.store.db.database";


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
        return getString(STORE_DB_HOST);
    }

    public int getPort() {
        return getInt(STORE_DB_PORT);
    }

    public String getUsername() {
        return getString(STORE_DB_USERNAME);
    }

    public String getAuth() {
        return getString(STORE_DB_AUTH);
    }

    public String getDatabase() {
        return getString(STORE_DB_DATABASE);
    }
}
