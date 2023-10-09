package io.ids.argus.job.base;

import io.grpc.Context;

public class JobGrpcContext {
    private static final Context.Key<ConnectorId> CONTEXT_CONNECTOR_ID_KEY = Context.key("connectorId");

    public static Context addConnectorId(ConnectorId module) {
        return Context.current().withValue(CONTEXT_CONNECTOR_ID_KEY, module);
    }

    public static ConnectorId getConnectorId() {
        return CONTEXT_CONNECTOR_ID_KEY.get();
    }
}
