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

    private final static Map<String, ArgusStoreSession> sessions = new ConcurrentHashMap<>();

    public void add(String requestId, ArgusStoreSession session) {
        sessions.put(requestId, session);
    }

    public ArgusStoreSession get(String uuid) {
        return sessions.get(uuid);
    }

    public String generateId() {
        return UUID.randomUUID().toString();
    }

    public ArgusStoreSession remove(String requestId) {
        return sessions.remove(requestId);
    }
}
