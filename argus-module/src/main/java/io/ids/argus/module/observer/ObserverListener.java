package io.ids.argus.module.observer;

import io.ids.argus.core.transport.grpc.OpenResponse;

public interface ObserverListener {
    /**
     * 接受到中心服务的请求
     * @param notice 中心服务的通知
     */
    void receive(OpenResponse.Notice notice);

    /**
     * 连接断开，重新建立连接
     */
    void reconnect();
}
