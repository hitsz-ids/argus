package io.ids.argus.store.server;

import io.ids.argus.core.conf.log.ArgusLogger;
import io.ids.argus.store.base.StoreAddress;
import io.ids.argus.store.server.db.conf.DbInstance;

import java.io.IOException;

/**
 * Bootstrap of Argus store server
 */
public class Bootstrap {
    private final ArgusStoreServer server;
    private final ArgusLogger log = new ArgusLogger(Bootstrap.class);

    Bootstrap() throws IOException {
        server = new ArgusStoreServer(new StoreAddress());
        DbInstance.get().initDb();
    }

    public void start() throws IOException, InterruptedException {
        server.start();
        log.debug("Argus Store Server Started.");
        server.awaitTermination();
    }

    public void shutdown() {
        server.shutdown();
    }
}
