package io.ids.argus.store.server.service;

import io.grpc.stub.StreamObserver;
import io.ids.argus.store.base.GrpcContext;
import io.ids.argus.store.grpc.OpenRequest;
import io.ids.argus.store.grpc.OpenResponse;
import io.ids.argus.store.grpc.SessionServiceGrpc;
import io.ids.argus.store.server.session.SessionManager;

/**
 * Session GRPC service
 */
public class SessionService extends SessionServiceGrpc.SessionServiceImplBase {

    @Override
    public StreamObserver<OpenRequest> open(StreamObserver<OpenResponse> responseObserver) {
        return new SessionObserver(SessionManager.get().generateId(),
                GrpcContext.getType(),
                responseObserver);
    }

}
