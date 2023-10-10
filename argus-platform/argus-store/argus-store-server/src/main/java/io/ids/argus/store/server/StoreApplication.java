package io.ids.argus.store.server;

import io.ids.argus.store.server.db.job.session.JobStoreService;

import java.io.IOException;

public class StoreApplication {
    public static void main(String[] args) {
        try {
            var bootstrap = new Bootstrap();
            bootstrap.start();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
