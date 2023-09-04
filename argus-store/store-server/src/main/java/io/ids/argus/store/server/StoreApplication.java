package io.ids.argus.store.server;

import io.ids.argus.core.base.conf.ArgusLogger;
import io.ids.argus.store.server.db.job.session.JobService;

import java.io.IOException;

public class StoreApplication {
    public static void main(String[] args) {
        var log = new ArgusLogger(StoreApplication.class);
        log.info("123");
        var bootstrap = new Bootstrap();
        bootstrap.addService(JobService.class);
        try {
            bootstrap.start();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
