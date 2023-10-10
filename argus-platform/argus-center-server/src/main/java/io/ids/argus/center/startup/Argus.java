package io.ids.argus.center.startup;

import io.ids.argus.center.exception.ArgusProtocolException;
import io.ids.argus.center.protocol.Dispatcher;
import io.ids.argus.center.protocol.Parser;
import io.ids.argus.center.protocol.ProtocolData;
import io.ids.argus.server.base.module.entity.Request;

public class Argus {
    private static final Argus instance = new Argus();
    private final ArgusCenterServer server;
    private final Dispatcher dispatcher;
    private Argus() {
        server = new ArgusCenterServer();
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

    public Request send(ProtocolData requestData) throws ArgusProtocolException {
        return dispatcher.dispatch(requestData);
    }

}
