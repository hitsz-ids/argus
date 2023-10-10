package io.ids.argus.job.base;


import io.ids.argus.core.conf.propertie.ArgusProperties;

public class JobProperties extends ArgusProperties {
    private final static JobProperties instance = new JobProperties();
    public static JobProperties get() {
        return instance;
    }
    @Override
    public String initPath() {
        return "argus.job.properties";
    }

    public int getPort() {
        return getInt("job.server.port");
    }

    public String getFD() {
        return getString("job.server.fd");
    }

}
