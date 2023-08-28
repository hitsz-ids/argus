package io.ids.argus.core.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class ArgusLogger {

    private final Logger log;

    public ArgusLogger(Class<?> clazz) {
        this.log = LoggerFactory.getLogger(clazz);
    }

    private static final String LOG_PREFIX = "[Argus] => ";

    public String format(String content) {
        return LOG_PREFIX + content;
    }

    public void info(String content, Object... params) {
        log.info(LOG_PREFIX + content, params);
    }

    public void debug(String content, Object... params) {
        log.debug(LOG_PREFIX + content, params);
    }

    public void error(String content, Exception e) {
        log.error(LOG_PREFIX + content, e);
    }

    public void error(String content, Object... params) {
        log.error(LOG_PREFIX + content, params);
    }
}
