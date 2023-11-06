package io.ids.argus.center.conf;


import io.ids.argus.core.conf.propertie.ArgusProperties;

/**
 * Properties of Center Server
 */
public class CenterProperties extends ArgusProperties {

    private static final CenterProperties instance = new CenterProperties();

    private static final String PROPERTIES_PATH         = "argus-center.properties";
    private static final String CA_PATH                 = "ca.public";
    private static final String SERVER_PUBLIC_KEY_PATH  = "server.public";
    private static final String SERVER_PRIVATE_KEY_PATH = "server.pkcs8";
    private static final String SERVER_PORT             = "center.server.port";
    private static final String SERVER_MODULE_PUB_DIR   = "server.module.pub.dir";
    private static final String HTTP_SERVER_PORT   = "http.server.port";


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
        return getInt(SERVER_PORT);
    }

    public String getPublicPath() {
        return getString(SERVER_PUBLIC_KEY_PATH);
    }

    public String getCaPublic() {
        return getString(CA_PATH);
    }

    public String getPKcs8() {
        return getString(SERVER_PRIVATE_KEY_PATH);
    }

    public String[] getModules() {
        return getString("modules").split(",");
    }

    public String getModulesDir() {
        return getString(SERVER_MODULE_PUB_DIR);
    }

    public String getHTTPServerPort() {
        return getString(HTTP_SERVER_PORT);
    }

}
