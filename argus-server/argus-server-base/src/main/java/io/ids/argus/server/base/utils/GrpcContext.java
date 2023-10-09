/*
 *
 */

package io.ids.argus.server.base.utils;


import io.grpc.Context;
import io.ids.argus.server.base.module.entity.Request;
import io.ids.argus.server.base.module.manager.ArgusModule;

public class GrpcContext {
    private GrpcContext() {
        throw new UnsupportedOperationException("GrpcContext");
    }

    private static final Context.Key<Request> REQUESTS = Context.key("request");
    private static final Context.Key<ArgusModule> MODULES = Context.key("modules");

    public static Context addRequest(Request request) {
        return Context.current().withValue(REQUESTS, request);
    }

    public static Request popRequest() {
        return REQUESTS.get();
    }

    public static ArgusModule getModule() {
        return MODULES.get();
    }

    public static Context addModule(Context context, ArgusModule module) {
        return context.withValue(MODULES, module);
    }
}