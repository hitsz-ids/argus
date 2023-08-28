package io.ids.argus.center.startup;

import io.ids.argus.core.common.Namespace;
import io.ids.argus.core.json.ArgusJson;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.UUID;

@Builder
@AllArgsConstructor
public class Command {
    public enum Type {
        FETCH
    }

    @Builder
    public record InternalData(String url, ArgusJson params, ArgusJson customized) {
    }

    private final String uuid = UUID.randomUUID().toString();
    private final InternalData data;
    private final Type type;
    private final String name;
    private final String version;
    private final Namespace namespace;
    public Command(Protocol protocol) {
        data = InternalData.builder()
                .customized(protocol.getCustomized())
                .params(protocol.getParams())
                .url(protocol.getUrl())
                .build();
        this.namespace = protocol.getNamespace();
        this.type = Type.FETCH;
        this.name = protocol.getModule();
        this.version = protocol.getVersion();
    }

    public String getUrl() {
        return data.url();
    }

    public ArgusJson getParams() {
        return data.params();
    }

    public ArgusJson getCustomized() {
        return data.customized();
    }

    public String getName() {
        return name;
    }

    public Namespace getNamespace() {
        return namespace;
    }

    public String getVersion() {
        return version;
    }

    public Type getType() {
        return type;
    }

    public String getUuid() {
        return uuid;
    }
}
