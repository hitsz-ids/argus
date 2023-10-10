package io.ids.argus.server.base.interceptor;

import io.grpc.*;
import io.ids.argus.grpc.base.Interceptor;
import io.ids.argus.server.base.exception.ArgusInterceptException;
import io.ids.argus.server.base.module.configuration.ModuleConfiguration;

public abstract class ServerBaseInterceptor extends Interceptor {
    protected final ModuleConfiguration configuration;

    public ServerBaseInterceptor(ModuleConfiguration configuration) {
        this.configuration = configuration;
    }

    protected abstract Context baseIntercept(Metadata metadata) throws ArgusInterceptException;
    @Override
    protected Context intercept(Metadata metadata) throws StatusRuntimeException {
        try {
            return baseIntercept(metadata);
        } catch (ArgusInterceptException e) {
            throw new StatusRuntimeException(Status.INTERNAL.withDescription(e.getMsg()));
        }
    }
}
