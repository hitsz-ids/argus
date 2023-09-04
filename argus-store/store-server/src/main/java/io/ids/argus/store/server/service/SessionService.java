package io.ids.argus.store.server.service;

import io.grpc.stub.StreamObserver;
import io.ids.argus.store.server.session.SessionManager;
import io.ids.argus.store.transport.GrpcUtils;
import io.ids.argus.store.transport.grpc.OpenRequest;
import io.ids.argus.store.transport.grpc.OpenResponse;
import io.ids.argus.store.transport.grpc.SessionServiceGrpc;

public class SessionService extends SessionServiceGrpc.SessionServiceImplBase {
    @Override
    public StreamObserver<OpenRequest> open(StreamObserver<OpenResponse> responseObserver) {
        return new SessionObserver(SessionManager.get().generateId(),
                GrpcUtils.getType(),
                responseObserver);
    }
}
