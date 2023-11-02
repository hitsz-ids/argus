package io.ids.argus.store.server.db.conf;

/**
 * Database type that Argus support
 * <p>
 * When add new Database instance should add definition here.
 */
public enum DbType {

    MYSQL("mysql", DbConstant.MYSQL_JDBC_URL, DbConstant.MYSQL_JDBC_DRIVER);

    private final String name;
    private final String url;
    private final String driver;

    DbType(String name, String url, String driver) {
        this.name = name;
        this.url = url;
        this.driver = driver;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getDriver() {
        return driver;
    }

}
