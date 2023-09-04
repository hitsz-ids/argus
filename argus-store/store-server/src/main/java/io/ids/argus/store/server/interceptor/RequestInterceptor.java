package io.ids.argus.store.server.interceptor;

import io.grpc.*;
import io.ids.argus.store.transport.GrpcUtils;
import org.apache.commons.lang3.StringUtils;

public class RequestInterceptor  implements ServerInterceptor {
    private String getHeader(String key, Metadata metadata) {
        return metadata.get(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER));
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call,
                                                                 Metadata metadata,
                                                                 ServerCallHandler<ReqT, RespT> handler) {
        String requestId = getHeader(GrpcUtils.HEADER_REQUEST_ID, metadata);
        if (StringUtils.isBlank(requestId)) {
            call.close(Status.INTERNAL.withDescription("RequestId参数错误"), metadata);
            return new ServerCall.Listener<>() {
            };
        }
        return Contexts.interceptCall(GrpcUtils.addRequestId(requestId), call, metadata, handler);
    }
}
