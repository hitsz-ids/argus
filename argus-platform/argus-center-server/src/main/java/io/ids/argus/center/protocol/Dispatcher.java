package io.ids.argus.center.protocol;

import io.ids.argus.server.base.module.entity.Request;
import io.ids.argus.server.base.module.entity.RequestBody;
import io.ids.argus.server.base.module.manager.ArgusModuleManager;

import java.util.Objects;

/**
 * Dispatch requests to specified Module API
 */
public class Dispatcher {

    public Request dispatch(Protocol protocol) {
        var argusModule = ArgusModuleManager.get().getModule(
                protocol.getModule(),
                protocol.getVersion());

        if (Objects.isNull(argusModule)) {
            //todo 模块未成功登录，调度失败
        }
        if (!argusModule.isReady()) {
            //todo 模块还未准备就绪，调度失败
        }
        return argusModule.request(RequestBody.builder()
                .url(protocol.getUrl())
                .customized(protocol.getCustomized())
                .params(protocol.getParams())
                .build());
    }

    public Request dispatch(ProtocolData data) {
        return dispatch(Parser.parse(data));
    }

}
