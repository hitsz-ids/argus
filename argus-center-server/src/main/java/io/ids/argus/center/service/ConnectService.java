package io.ids.argus.center.service;

import io.grpc.stub.StreamObserver;
import io.ids.argus.center.common.GrpcContext;
import io.ids.argus.core.transport.grpc.ArgusServiceGrpc;
import io.ids.argus.core.transport.grpc.OpenRequest;
import io.ids.argus.core.transport.grpc.OpenResponse;

public class ConnectService extends ArgusServiceGrpc.ArgusServiceImplBase {
    @Override
    public StreamObserver<OpenRequest> open(StreamObserver<OpenResponse> responseObserver) {
        return new Connector(GrpcContext.getModule(), responseObserver);
    }
}
