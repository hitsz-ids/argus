package io.ids.argus.store.server.service;

import io.ids.argus.store.base.GrpcContext;
import io.ids.argus.store.server.exception.error.SessionError;
import io.ids.argus.store.server.exception.ArgusSessionException;
import io.ids.argus.store.server.session.ArgusSqlSession;
import io.ids.argus.store.server.session.SessionManager;

import java.util.Objects;

public interface IService<T extends ArgusSqlSession<?>> {
    default T getSqlSession() {
        var session = SessionManager.get().get(GrpcContext.getRequestId());
        if (Objects.isNull(session)) {
            throw new ArgusSessionException(SessionError.NOT_FOUND_SESSION);
        }
        return (T) session;
    }
}
