package io.ids.argus.center.startup;

import io.ids.argus.core.base.json.ArgusJson;
import io.ids.argus.store.client.Store;
import io.ids.argus.store.client.session.JobSession;

import java.net.URISyntaxException;

public class Argus {
    private static final Argus instance = new Argus();
    private final Bootstrap server;
    private final Dispatcher dispatcher;

    private Argus() {
        server = new Bootstrap();
        dispatcher = new Dispatcher();
    }

    public static Argus get() {
        return instance;
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
