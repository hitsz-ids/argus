package io.ids.argus.center.startup;

import io.ids.argus.core.json.ArgusJson;

import java.net.URISyntaxException;

public class Argus {
    private static final Argus instance = new Argus();

    public static Argus get() {
        return instance;
    }
    private final Bootstrap server;
    private final Dispatcher dispatcher;
    Argus() {
        server = new Bootstrap();
        dispatcher = new Dispatcher();
    }
    public static void start(Class<?> primarySource) {
        try {
            get().server.start(primarySource);
            get().server.awaitTermination();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public ArgusJson send(RequestData requestData) throws URISyntaxException {
        return dispatcher.dispatch(Parser.parse(requestData));
    }
}
