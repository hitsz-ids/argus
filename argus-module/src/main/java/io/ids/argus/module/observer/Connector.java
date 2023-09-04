package io.ids.argus.module.observer;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import io.ids.argus.core.base.conf.ArgusLogger;
import io.ids.argus.core.transport.grpc.ArgusModule;
import io.ids.argus.core.transport.grpc.OpenResponse;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class Connector implements StreamObserver<OpenResponse> {
    private final ArgusLogger log = new ArgusLogger(Connector.class);
    private final CountDownLatch countDownLatch = new CountDownLatch(1);
    private final ArgusModule module;
    private Status status;
    private final ObserverListener listener;

    public Connector(ArgusModule module, ObserverListener listener) {
        this.module = module;
        this.listener = listener;
    }

    private void login(OpenResponse.Login login) {
        if (!Objects.equals(status, io.grpc.Status.OK)) {
            if (Objects.equals(login.getModule(), module)) {
                status = io.grpc.Status.OK;
                countDownLatch.countDown();
                log.info("模块[{}:{}]已成功链接到中心服务",
                        module.getName(),
                        module.getVersion());
            } else {
                status = io.grpc.Status.UNAUTHENTICATED;
            }
        }
    }

    private void consume(OpenResponse stream) {
        if (stream.getDataCase() == OpenResponse.DataCase.LOGIN) {
            login(stream.getLogin());
        } else {
            listener.receive(stream.getNotice());
        }
    }

    @Override
    public void onNext(OpenResponse stream) {
        try {
            consume(stream);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new StatusRuntimeException(Status.INTERNAL);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        if (throwable instanceof StatusRuntimeException exception) {
            status = exception.getStatus();
            log.debug("模块[{}:{}]连接中心服务已经断开，请检查中心服务是否异常",
                    module.getName(),
                    module.getVersion());
        }
        listener.reconnect();
    }

    @Override
    public void onCompleted() {
        //noop 为了保持状态，双向流不会关闭
    }

}
