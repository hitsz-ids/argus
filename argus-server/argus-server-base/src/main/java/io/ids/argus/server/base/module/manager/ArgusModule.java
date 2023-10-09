package io.ids.argus.server.base.module.manager;


import io.ids.argus.core.grpc.RequestData;
import io.ids.argus.server.base.exception.error.ModuleError;
import io.ids.argus.server.base.exception.ArgusModuleException;
import io.ids.argus.server.base.module.entity.Request;
import io.ids.argus.server.base.module.entity.RequestBody;
import io.ids.argus.server.base.utils.ServerConstants;
import lombok.*;

import java.util.Objects;

@Builder
@Data
@AllArgsConstructor
public class ArgusModule {
    private final String name;
    private final String version;
    private Connector connector;

    public ArgusModule(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public void bind(Connector connector) {
        this.connector = connector;
    }

    public boolean isReady() {
        return !Objects.isNull(connector) && connector.isReady();
    }

    public Request request(RequestBody body) {
        var requestId = RequestManager.generateId();
        var request = new Request(body);
        RequestManager.get().stash(requestId, request);
        connector.call(
                RequestData.newBuilder()
                        .setRequestId(requestId)
                        .build()
        );
        try {
            var pass = request.await(ServerConstants.REQUEST_TIME_OUT);
            if (!pass) {
                throw new ArgusModuleException(ModuleError.ERROR_REQUEST_TIME_OUT);
            }
        } catch (InterruptedException e) {
            throw new ArgusModuleException(ModuleError.ERROR_REQUEST_INTERCEPTED);
        }
        return request;
    }
}
