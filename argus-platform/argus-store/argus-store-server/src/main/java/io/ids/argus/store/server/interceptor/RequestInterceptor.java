package io.ids.argus.store.server.interceptor;

import io.grpc.*;
import io.ids.argus.store.base.GrpcContext;
import io.ids.argus.store.server.session.SessionManager;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class RequestInterceptor  implements ServerInterceptor {
    private String getHeader(String key, Metadata metadata) {
        return metadata.get(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER));
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call,
                                                                 Metadata metadata,
                                                                 ServerCallHandler<ReqT, RespT> handler) {
        String requestId = getHeader(GrpcContext.HEADER_REQUEST_ID, metadata);
        if (StringUtils.isBlank(requestId)) {
            call.close(Status.INTERNAL.withDescription("RequestId参数错误"), metadata);
            return new ServerCall.Listener<>() {
            };
        }
        var session = SessionManager.get().get(requestId);
        if (Objects.isNull(session)) {
            call.close(Status.INTERNAL.withDescription("RequestId参数错误"), metadata);
            return new ServerCall.Listener<>() {
            };
        }

        return Contexts.interceptCall(GrpcContext.addRequestId(requestId), call, metadata, handler);
    }
}
