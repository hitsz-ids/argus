package io.ids.argus.job.server;

import io.ids.argus.core.base.callback.UnaryCallback;
import io.ids.argus.core.conf.log.ArgusLogger;
import io.ids.argus.job.base.JobAddress;
import io.ids.argus.store.client.ArgusStore;

import java.io.IOException;

public class Bootstrap {
    private final ArgusJobServer server;
    private final ArgusLogger log = new ArgusLogger(Bootstrap.class);
    Bootstrap() throws IOException {
        server = new ArgusJobServer(new JobAddress());
        ArgusStore.init();
    }

    public void start(UnaryCallback callback) throws IOException, InterruptedException {
        server.start();
        callback.call();
        log.debug("ArgusJobServer started successfully.");
        server.awaitTermination();
    }

    public void shutdown() {
        server.shutdown();
    }
}
