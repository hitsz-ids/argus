package io.ids.argus.store.server.db.conf;

public enum DbType {
    MYSQL("mysql",
            "jdbc:mysql://%s:%s/%s?zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&rewriteBatchedStatements=true&tinyInt1isBit=false&useSSL=false",
            "com.mysql.cj.jdbc.Driver");
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
