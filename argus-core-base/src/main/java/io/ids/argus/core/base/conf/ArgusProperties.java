package io.ids.argus.core.base.conf;

import io.ids.argus.core.base.exception.ArgusCreateClassException;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public abstract class ArgusProperties {
    private final ArgusLogger log = new ArgusLogger(ArgusProperties.class);
    private final Properties properties;

    public abstract String initPath();

    protected ArgusProperties() {
        try {
            var path = initPath();
            properties = new Properties();
            var inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
            if (inputStream == null) {
                throw new IOException(String.format("%s文件不存在", path));
            }
            properties.load(inputStream);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new ArgusCreateClassException();
        }
    }

    protected String getString(String key) {
        String envKey = key.toUpperCase();
        Map<String, String> envs = System.getenv();
        String result = envs.get(envKey);
        if (result == null) {
            result = properties.getProperty(key);
        }
        return result == null ? "" : result;
    }

    protected Integer getInt(String key) {
        String str = getString(key);
        try {
            return StringUtils.isBlank(str) ? null : Integer.parseInt(str);
        } catch (NumberFormatException e) {
            log.error(e.getMessage(), e);
        }
        return 0;
    }
}
