package io.ids.argus.server.base.module.manager;

import com.google.protobuf.Message;

public interface Connector {

    boolean isReady();

    void call(Message message);
}
