package io.ids.argus.store.server.service;

import io.ids.argus.store.server.common.SessionStatus;
import io.ids.argus.store.server.exception.ArgusSessionException;
import io.ids.argus.store.server.session.ArgusSession;
import io.ids.argus.store.server.session.SessionManager;
import io.ids.argus.store.transport.GrpcUtils;

import java.util.Objects;

public interface IService<T extends ArgusSession<?>> {
    default T getSession() {
        var session = SessionManager.get().get(GrpcUtils.getRequestId());
        if (Objects.isNull(session)) {
            throw new ArgusSessionException(SessionStatus.NOT_FOUND_SESSION);
        }
        return (T) session;
    }
}
