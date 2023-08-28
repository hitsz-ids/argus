/*
 *
 */

package io.ids.argus.center.context;

import io.grpc.Context;
import io.ids.argus.center.service.RequestManager;
import io.ids.argus.core.grpc.ArgusModule;

public class GrpcContext {
    private GrpcContext() {
        throw new UnsupportedOperationException("GrpcContext");
    }

    private static final Context.Key<RequestManager.Request> REQUESTS = Context.key("request");
    private static final Context.Key<ArgusModule> MODULES = Context.key("modules");

    public static Context addRequest(RequestManager.Request request) {
        return Context.current().withValue(REQUESTS, request);
    }

    public static RequestManager.Request popRequest() {
        return REQUESTS.get();
    }

    public static ArgusModule getModule() {
        return MODULES.get();
    }

    public static Context addModule(Context context, ArgusModule module) {
        return context.withValue(MODULES, module);
    }
}