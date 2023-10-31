package io.ids.argus.store.server.interceptor;

import io.grpc.*;
import io.ids.argus.core.conf.utils.Utils;
import io.ids.argus.store.base.GrpcContext;
import io.ids.argus.store.grpc.SessionType;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Objects;

/**
 * Interceptor of Open Session Request
 */
public class SessionInterceptor implements ServerInterceptor {

    private byte[] getHeader(Metadata metadata) {
        return metadata.get(Metadata.Key.of(GrpcContext.HEADER_SESSION_TYPE, Metadata.BINARY_BYTE_MARSHALLER));
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call,
                                                                 Metadata metadata,
                                                                 ServerCallHandler<ReqT, RespT> handler) {
        byte[] type = getHeader(metadata);
        if (ArrayUtils.isEmpty(type) || type.length > 4) {
            call.close(Status.INTERNAL.withDescription("SessionType parameter error."), metadata);
            return new ServerCall.Listener<>() {
            };
        }

        var sessionType = SessionType.forNumber(Utils.bytesToInt(type));
        if (Objects.isNull(sessionType)) {
            call.close(Status.UNKNOWN.withDescription("Unknown SessionType."), metadata);
            return new ServerCall.Listener<>() {
            };
        }
        return Contexts.interceptCall(GrpcContext.addType(sessionType), call, metadata, handler);
    }
}
