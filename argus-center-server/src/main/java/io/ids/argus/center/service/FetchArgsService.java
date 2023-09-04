package io.ids.argus.center.service;

import io.grpc.stub.StreamObserver;
import io.ids.argus.center.common.GrpcContext;
import io.ids.argus.core.transport.grpc.FetchRequest;
import io.ids.argus.core.transport.grpc.FetchResponse;
import io.ids.argus.core.transport.grpc.FetchServiceGrpc;

public class FetchArgsService extends FetchServiceGrpc.FetchServiceImplBase {
    @Override
    public StreamObserver<FetchRequest> fetch(StreamObserver<FetchResponse> responseObserver) {
        return new FetchArgsObserver(GrpcContext.popRequest(), responseObserver);
    }
}
