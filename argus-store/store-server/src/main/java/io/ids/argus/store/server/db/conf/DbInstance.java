package io.ids.argus.store.server.db.conf;

import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.IOException;
import java.util.Properties;

public class DbInstance {
    private static DbInstance instance = new DbInstance();

    private DbInstance() {
    }

    public static DbInstance get() {
        return instance;
    }

    private SqlSessionFactory sqlSessionFactory;

    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

    public void initDb() throws IOException {
        var conf = new Configuration();
        var properties = new Properties();
        properties.put("jdbc.driver", DbType.MYSQL.getDriver());
        properties.put("jdbc.url", String.format(DbType.MYSQL.getUrl(),
                conf.getHost(),
                conf.getPort(),
                conf.getDatabase()));
        properties.put("jdbc.username", conf.getUsername());
        properties.put("jdbc.password", conf.getAuth());
        var resource = conf.getResource();
        try (var inputStream = Resources.getResourceAsStream(resource)) {
            sqlSessionFactory = new MybatisSqlSessionFactoryBuilder().build(inputStream, properties);
        }
    }
}
