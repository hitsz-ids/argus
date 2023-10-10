package io.ids.argus.module.observer;


import io.ids.argus.core.grpc.RequestData;

public interface ObserverListener {
    /**
     * 接受到中心服务的请求
     *
     * @param data 服务端发送的请求参数
     */
    void receive(RequestData data);

    void connected();
    /**
     * 连接断开，重新建立连接
     */
    void error(Throwable throwable);

    void completed();
}
