package io.ids.argus.store.server.db.conf;

/**
 * The Constant of Argus store
 */
class DbConstant {

    // MYSQL connect settings
    public static final String MYSQL_JDBC_URL    = "jdbc:mysql://%s:%s/%s?zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&rewriteBatchedStatements=true&tinyInt1isBit=false&useSSL=false";
    public static final String MYSQL_JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";


}
