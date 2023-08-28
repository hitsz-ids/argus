package io.ids.argus.center.service;

import io.grpc.stub.StreamObserver;
import io.ids.argus.center.context.GrpcContext;
import io.ids.argus.core.grpc.FetchRequest;
import io.ids.argus.core.grpc.FetchResponse;
import io.ids.argus.core.grpc.FetchServiceGrpc;

public class FetchArgsService extends FetchServiceGrpc.FetchServiceImplBase {
    @Override
    public StreamObserver<FetchRequest> fetch(StreamObserver<FetchResponse> responseObserver) {
        return new FetchArgsObserver(GrpcContext.popRequest(), responseObserver);
    }
}
