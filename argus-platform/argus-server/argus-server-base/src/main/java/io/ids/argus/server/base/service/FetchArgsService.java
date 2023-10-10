package io.ids.argus.server.base.service;

import io.grpc.stub.StreamObserver;
import io.ids.argus.core.grpc.FetchRequest;
import io.ids.argus.core.grpc.FetchResponse;
import io.ids.argus.core.grpc.FetchServiceGrpc;
import io.ids.argus.server.base.utils.GrpcContext;

public class FetchArgsService extends FetchServiceGrpc.FetchServiceImplBase {
    @Override
    public StreamObserver<FetchRequest> fetch(StreamObserver<FetchResponse> responseObserver) {
        return new FetchServiceObserver(GrpcContext.popRequest(), responseObserver);
    }
}
