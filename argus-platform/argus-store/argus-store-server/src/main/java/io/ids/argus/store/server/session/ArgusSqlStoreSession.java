package io.ids.argus.store.server.session;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.ids.argus.core.conf.log.ArgusLogger;
import io.ids.argus.store.server.exception.error.DatabaseError;
import io.ids.argus.store.server.db.conf.DbInstance;
import io.ids.argus.store.server.exception.ArgusDatabaseException;
import org.apache.ibatis.session.SqlSession;

public abstract class ArgusSqlStoreSession<M extends BaseMapper<?>> extends ArgusStoreSession implements AutoCloseable {
    private static final ArgusLogger log = new ArgusLogger(ArgusSqlStoreSession.class);

    protected final M mapper;
    private final SqlSession sqlSession;

    protected ArgusSqlStoreSession() {
        sqlSession = DbInstance.get().getSqlSessionFactory().openSession();
        try {
            mapper = sqlSession.getMapper(getMapper());
        } catch (Exception e) {
            sqlSession.close();
            log.error(e.getMessage(), e);
            throw new ArgusDatabaseException(DatabaseError.DATABASE_SESSION_GET_MAPPER_ERROR);
        }
    }

    public abstract Class<M> getMapper();

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
