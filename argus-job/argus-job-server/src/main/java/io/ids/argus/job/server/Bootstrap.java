package io.ids.argus.job.server;

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

    public void start() throws IOException, InterruptedException {
        server.start();
        log.debug("ArgusJobServer已成功启动");
        server.awaitTermination();
    }

    public void shutdown() {
        server.shutdown();
    }
}
