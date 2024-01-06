package io.ids.argus.store.server.db.conf;

import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * The Database instance of argus store
 * <p>
 * Now it is MySQL default.
 */
public class DbInstance {
    private static final DbInstance instance = new DbInstance();

    private DbInstance() {
    }

    public static DbInstance get() {
        System.out.println("instance:"+instance);
        return instance;
    }

    private SqlSessionFactory sqlSessionFactory;

    public SqlSessionFactory getSqlSessionFactory() {
        System.out.println("sqlSessionFactory:"+sqlSessionFactory);
        return sqlSessionFactory;
    }

    public void initDb() throws IOException {
        var conf = new Configuration();
        var jdbcUrl = String.format(DbType.MYSQL.getUrl(), conf.getHost(), conf.getPort(), conf.getDatabase());

        var properties = new Properties();
        properties.put("jdbc.driver", DbType.MYSQL.getDriver());
        properties.put("jdbc.url", jdbcUrl);
        properties.put("jdbc.username", conf.getUsername());
        properties.put("jdbc.password", conf.getAuth());

        var resource = conf.getResource();
        try (var inputStream = Resources.getResourceAsStream(resource)) {
            sqlSessionFactory = new MybatisSqlSessionFactoryBuilder().build(inputStream, properties);
        }
    }
}
