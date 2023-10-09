package io.ids.argus.job.server.manager;

import io.grpc.stub.StreamObserver;
import io.ids.argus.job.base.ConnectorId;
import io.ids.argus.job.grpc.JobOpenResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectorManager {
    private static final ConnectorManager instance = new ConnectorManager();

    private final Map<ConnectorId, Connector> connectors = new ConcurrentHashMap<>();
    private ConnectorManager() {
    }

    public static ConnectorManager get() {
        return instance;
    }

    public void bind(Connector connector) {
        connectors.put(connector.getId(), connector);
    }

    public void unBind(Connector connector) {
        connectors.remove(connector.getId());
    }

    public Connector get(ConnectorId connectorId) {
        return connectors.get(connectorId);
    }

    public Connector createConnector(ConnectorId connectorId,
                                     StreamObserver<JobOpenResponse> responseObserver) {
        return new Connector(responseObserver, connectorId);
    }
}
