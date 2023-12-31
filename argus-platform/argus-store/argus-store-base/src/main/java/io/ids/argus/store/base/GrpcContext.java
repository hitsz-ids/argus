/*
 *
 */

package io.ids.argus.store.base;


import io.grpc.Context;
import io.ids.argus.store.grpc.SessionType;

/**
 * The Context of Argus store layer
 */
public class GrpcContext {
    private GrpcContext() {
        throw new UnsupportedOperationException("GrpcCommon");
    }

    public static final String HEADER_SESSION_TYPE = "session_type-bin";

    public static final String HEADER_REQUEST_ID = "request_id";

    private static final Context.Key<SessionType> CONTEXT_TYPE_KEY = Context.key("type");

    private static final Context.Key<String> CONTEXT_REQUEST_ID_KEY = Context.key("requestId");

    public static Context addType(SessionType type) {
        return Context.current().withValue(CONTEXT_TYPE_KEY, type);
    }

    public static Context addRequestId(String requestId) {
        return Context.current().withValue(CONTEXT_REQUEST_ID_KEY, requestId);
    }

    public static String getRequestId() {
        return CONTEXT_REQUEST_ID_KEY.get();
    }

    public static SessionType getType() {
        return CONTEXT_TYPE_KEY.get();
    }
}