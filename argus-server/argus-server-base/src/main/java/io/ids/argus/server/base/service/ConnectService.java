package io.ids.argus.server.base.service;

import io.grpc.stub.StreamObserver;
import io.ids.argus.core.grpc.ArgusServiceGrpc;
import io.ids.argus.core.grpc.OpenRequest;
import io.ids.argus.core.grpc.OpenResponse;
import io.ids.argus.server.base.utils.GrpcContext;

public class ConnectService extends ArgusServiceGrpc.ArgusServiceImplBase {
    @Override
    public StreamObserver<OpenRequest> open(StreamObserver<OpenResponse> responseObserver) {
        // 登录对应模块
        var argusModule  = GrpcContext.getModule();
        return new ConnectObserver(argusModule, responseObserver);
    }
}
