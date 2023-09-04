package io.ids.argus.center.conf;

import io.ids.argus.core.base.conf.ArgusProperties;

public class CenterProperties extends ArgusProperties {
    private static final String PROPERTIES_PATH = "argus-center.properties";

    private static final CenterProperties instance = new CenterProperties();

    private CenterProperties() {
    }

    public static CenterProperties get() {
        return instance;
    }

    @Override
    public String initPath() {
        return PROPERTIES_PATH;
    }

    public int getPort() {
        return getInt("server.port");
    }

    public String getPublicPath() {
        return getString("server.public");
    }

    public String getCaPublic() {
        return getString("ca.public");
    }

    public String getPKcs8() {
        return getString("server.pkcs8");
    }

    public String[] getModules() {
        return getString("modules").split(",");
    }

    public String getModulesDir() {
        return getString("server.module.pub.dir");
    }
}
