package io.ids.argus.center.interceptor;

import io.grpc.Context;
import io.grpc.Metadata;
import io.ids.argus.center.common.NetworkStatus;
import io.ids.argus.center.context.GrpcContext;
import io.ids.argus.center.exception.ArgusInterceptException;
import io.ids.argus.center.service.RequestManager;
import io.ids.argus.core.common.GrpcCommon;

import java.util.Objects;

public class DataFetchInterceptor extends Interceptor {
    @Override
    protected Context intercept(Metadata metadata) throws ArgusInterceptException {
        String requestId = getHeader(GrpcCommon.HEADER_REQUEST_ID, metadata);
        var request = RequestManager.get().pop(requestId);
        if (Objects.isNull(request)) {
            throw new ArgusInterceptException(NetworkStatus.NOT_FOUND_COMMAND);
        }
        return GrpcContext.addRequest(request);
    }
}
