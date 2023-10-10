package io.ids.argus.grpc.base;

import io.grpc.*;

public abstract class Interceptor implements ServerInterceptor {

    protected abstract Context intercept(Metadata metadata) throws StatusRuntimeException;

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call,
                                                                 Metadata metadata,
                                                                 ServerCallHandler<ReqT, RespT> serverCallHandler) {
        Context context;
        try {
            context = intercept(metadata);
        } catch (StatusRuntimeException e) {
            call.close(e.getStatus(), metadata);
            return new ServerCall.Listener<>() {
            };
        }
        return Contexts.interceptCall(context, call, metadata, serverCallHandler);
    }
}
