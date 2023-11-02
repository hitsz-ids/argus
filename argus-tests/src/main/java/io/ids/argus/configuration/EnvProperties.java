package io.ids.argus.configuration;

import io.ids.argus.core.conf.propertie.ArgusProperties;

/**
 * @author jalr4real[jalrhex@gmail.com]
 * @since 2023-11-02
 */
public class EnvProperties extends ArgusProperties {
    private static final String PROPERTIES_PATH = "env.properties";

    private static final EnvProperties instance = new EnvProperties();

    private EnvProperties() {
    }

    public static EnvProperties get() {
        return instance;
    }

    @Override
    public String initPath() {
        return PROPERTIES_PATH;
    }

    public String getDemoHost() {
        return getString("demo.service.host");
    }
}
