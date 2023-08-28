package io.ids.argus.center.service;

import io.grpc.stub.StreamObserver;
import io.ids.argus.center.context.GrpcContext;
import io.ids.argus.core.grpc.ArgusServiceGrpc;
import io.ids.argus.core.grpc.OpenRequest;
import io.ids.argus.core.grpc.OpenResponse;

public class ConnectService extends ArgusServiceGrpc.ArgusServiceImplBase {
    @Override
    public StreamObserver<OpenRequest> open(StreamObserver<OpenResponse> responseObserver) {
        return new Connection(GrpcContext.getModule(), responseObserver);
    }
}
