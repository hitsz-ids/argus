package io.ids.argus.store.server;

import java.io.IOException;

/**
 * Argus store application starts here
 */
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
