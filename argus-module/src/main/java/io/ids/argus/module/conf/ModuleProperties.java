package io.ids.argus.module.conf;

import io.ids.argus.core.conf.ArgusProperties;

public class ModuleProperties extends ArgusProperties {

    private static final String PROPERTIES_PATH = "argus-module.properties";

    private static final ModuleProperties instance = new ModuleProperties();

    public static ModuleProperties get() {
        return instance;
    }
    @Override
    public String initPath() {
        return PROPERTIES_PATH;
    }

    public String getPublicPath() {
        return getString("module.public");
    }

    public String getCaPublic() {
        return getString("ca.public");
    }

    public String getPKcs8() {
        return getString("module.pkcs8");
    }

    public String getCenterHost() {
        return getString("center.host");
    }

    public Integer getCenterPort() {
        return getInt("center.port");
    }
    public String getName() {
        return getString("module.name");
    }

    public String getVersion() {
        return getString("module.version");
    }
}
