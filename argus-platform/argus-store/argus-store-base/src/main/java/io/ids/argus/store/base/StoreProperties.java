package io.ids.argus.store.base;


import io.ids.argus.core.conf.propertie.ArgusProperties;

/**
 * Argus store layer properties
 */
public class StoreProperties extends ArgusProperties {
    private final static StoreProperties instance = new StoreProperties();
    public static StoreProperties get() {
        return instance;
    }
    @Override
    public String initPath() {
        return "argus.store.properties";
    }

    public int getPort() {
        return getInt("store.server.port");
    }

    public String getFD() {
        return getString("store.server.fd");
    }

}
