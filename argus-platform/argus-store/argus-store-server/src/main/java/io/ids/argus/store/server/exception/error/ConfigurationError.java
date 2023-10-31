package io.ids.argus.store.server.exception.error;


import io.ids.argus.core.conf.exception.IError;

/**
 * Argus Store Configuration error code
 */
public enum ConfigurationError implements IError {
    CONF_HOST_ERROR         (30001, "The host field is configured incorrectly, [argus.store.host] is not configured."),
    CONF_DATABASE_ERROR     (30002, "The database field is configured incorrectly, [argus.store.database] is not configured."),
    CONF_USERNAME_ERROR     (30003, "The username field is configured incorrectly, [argus.store.username] is not configured."),
    CONF_AUTH_ERROR         (30004, "The auth field is configured incorrectly, [argus.store.auth] is not configured."),
    ;

    private final int    code;
    private final String msg;

    ConfigurationError(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String getMsg() {
        return msg;
    }

    @Override
    public int getCode() {
        return code;
    }
}
