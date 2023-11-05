package io.ids.argus.module.observer;


import io.ids.argus.core.grpc.RequestData;

public interface ObserverListener {

    /**
     * Receive the request of Central Service
     *
     * @param data params of central service
     */
    void receive(RequestData data);

    void connected();

    /**
     * Reconnect after connect was lost
     */
    void error(Throwable throwable);

    void completed();
}
