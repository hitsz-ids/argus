package io.ids.argus.store.server.session;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.ids.argus.core.base.conf.ArgusLogger;
import io.ids.argus.store.server.common.DatabaseStatus;
import io.ids.argus.store.server.db.conf.DbInstance;
import io.ids.argus.store.server.exception.ArgusDatabaseException;
import org.apache.ibatis.session.SqlSession;

public abstract class ArgusSession<M extends BaseMapper<?>> implements AutoCloseable {
    protected final M mapper;
    private final SqlSession sqlSession;

    private static final ArgusLogger log = new ArgusLogger(ArgusSession.class);

    protected ArgusSession(Class<M> clazz) {
        sqlSession = DbInstance.get().getSqlSessionFactory().openSession();
        try {
            mapper = sqlSession.getMapper(clazz);
        } catch (Exception e) {
            sqlSession.close();
            log.error(e.getMessage(), e);
            throw new ArgusDatabaseException(DatabaseStatus.DATABASE_SESSION_GET_MAPPER_ERROR);
        }
    }

    @Override
    public void close() {
        sqlSession.close();
    }

    public void commit() {
        sqlSession.commit();
    }

    public void rollback() {
        sqlSession.rollback(true);
    }
}
