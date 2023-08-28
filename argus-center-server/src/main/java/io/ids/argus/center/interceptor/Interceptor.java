package io.ids.argus.center.interceptor;

import io.grpc.*;
import io.ids.argus.center.exception.ArgusInterceptException;

public abstract class Interceptor implements ServerInterceptor {
    protected String getHeader(String key, Metadata metadata) {
        return metadata.get(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER));
    }

    protected abstract Context intercept(Metadata metadata) throws ArgusInterceptException;

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call,
                                                                 Metadata metadata,
                                                                 ServerCallHandler<ReqT, RespT> serverCallHandler) {
        Context context;
        try {
            context = intercept(metadata);
        } catch (ArgusInterceptException e) {
            call.close(Status.INTERNAL.withDescription(e.getMsg()), metadata);
            return new ServerCall.Listener<>() {
            };
        }
        return Contexts.interceptCall(context, call, metadata, serverCallHandler);
    }
}
