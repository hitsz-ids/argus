package io.ids.argus.store.client;

import io.grpc.Metadata;
import io.ids.argus.store.client.network.StoreChannelPool;

import java.io.IOException;

public final class Store {
    private static final Store instance = new Store();
    private static final Metadata.Key<String> spaceHeader =
            Metadata.Key.of("space",
                    Metadata.ASCII_STRING_MARSHALLER);

    private Store() {
    }

    public static Store get() {
        return instance;
    }

    public void init() throws IOException {
        StoreChannelPool.get().init();
    }
}
