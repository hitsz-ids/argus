package io.ids.argus.server.base.interceptor;

import io.grpc.Context;
import io.grpc.Metadata;
import io.ids.argus.server.base.exception.error.NetworkError;
import io.ids.argus.server.base.exception.ArgusInterceptException;
import io.ids.argus.server.base.module.configuration.ModuleConfiguration;
import io.ids.argus.server.base.module.manager.RequestManager;
import io.ids.argus.server.base.utils.GrpcContext;
import io.ids.argus.server.grpc.GrpcConstants;

import java.util.Objects;

public class FetchServiceInterceptor extends ServerBaseInterceptor {
    public FetchServiceInterceptor(ModuleConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected Context baseIntercept(Metadata metadata) throws ArgusInterceptException {
        String requestId = metadata.get(GrpcConstants.HEADER_REQUEST_ID);
        var request = RequestManager.get().pop(requestId);
        if (Objects.isNull(request)) {
            throw new ArgusInterceptException(NetworkError.NOT_FOUND_COMMAND);
        }
        if (request.isException()) {
            throw new ArgusInterceptException(NetworkError.REQUEST_IS_END);
        }
        return GrpcContext.addRequest(request);
    }
}
