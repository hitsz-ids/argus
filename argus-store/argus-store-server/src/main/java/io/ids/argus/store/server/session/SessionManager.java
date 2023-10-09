package io.ids.argus.store.server.session;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private static final SessionManager instance = new SessionManager();

    private SessionManager() {
    }

    public static SessionManager get() {
        return instance;
    }

    private final static Map<String, ArgusSqlSession<?>> sessions = new ConcurrentHashMap<>();

    public void add(String requestId, ArgusSqlSession<?> session) {
        sessions.put(requestId, session);
    }

    public ArgusSqlSession<?> get(String uuid) {
        return sessions.get(uuid);
    }

    public String generateId() {
        return UUID.randomUUID().toString();
    }

    public ArgusSqlSession<?> remove(String requestId) {
        return sessions.remove(requestId);
    }
}
