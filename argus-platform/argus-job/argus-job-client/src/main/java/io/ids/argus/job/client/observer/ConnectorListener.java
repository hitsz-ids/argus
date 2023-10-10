package io.ids.argus.job.client.observer;

import com.google.protobuf.Any;

public interface ConnectorListener {
    void reconnect();

    boolean receive(String seq, Any any) throws Exception;
}
