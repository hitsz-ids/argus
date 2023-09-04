package io.ids.argus.center.startup;

import io.ids.argus.center.module.ModuleManager;
import io.ids.argus.center.service.RequestManager;
import io.ids.argus.core.base.json.ArgusJson;

import java.util.Objects;

public class Dispatcher {
    ArgusJson dispatch(Protocol protocol) {
        return produce(protocol);
    }

    private ArgusJson produce(Command command) {
        var connection = ModuleManager.get().getConnection(command.getName(),
                command.getVersion());
        if (Objects.isNull(connection) || !connection.ok()) {
            return null;
        }
        return RequestManager.get().execute(connection, command);
    }

    private ArgusJson produce(Protocol protocol) {
        return produce(new Command(protocol));
    }
}
