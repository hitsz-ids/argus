package io.ids.argus.server.base.module.manager;

import io.ids.argus.server.base.module.entity.Request;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RequestManager {

    private static final RequestManager instance = new RequestManager();

    private final Map<String, Request> requests = new ConcurrentHashMap<>();
    public static RequestManager get() {
        return instance;
    }
    public static String generateId() {
        return UUID.randomUUID().toString();
    }

    public void stash(String id, Request request) {
        requests.put(id, request);
    }

    public Request pop(String id) {
        return requests.remove(id);
    }

    public Request get(String id) {
        return requests.get(id);
    }
}
