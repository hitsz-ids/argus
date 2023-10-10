package io.ids.argus.job.server;

import java.io.IOException;

public class JobApplication {
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
