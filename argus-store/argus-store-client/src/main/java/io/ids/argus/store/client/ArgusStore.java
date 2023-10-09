package io.ids.argus.store.client;

import io.ids.argus.store.base.StoreAddress;
import io.ids.argus.store.client.session.StoreSession;

import java.io.IOException;

public final class ArgusStore {
    private static ArgusStoreClient client;
    public static final ArgusStore instance = new ArgusStore();

    private ArgusStore() {
    }

    public static ArgusStore get() {
        return instance;
    }
    public static void init() throws IOException {
        client = new ArgusStoreClient(new StoreAddress());
    }


    public <S extends StoreSession<?>> S open(Class<S> sClass) {
        return client.openJobSession(sClass);
    }
}
