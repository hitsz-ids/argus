package io.ids.argus.module.observer;


import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import io.ids.argus.core.conf.log.ArgusLogger;
import io.ids.argus.core.grpc.LoginData;
import io.ids.argus.core.grpc.OpenResponse;
import io.ids.argus.core.grpc.RequestData;

public class ConnectObserver implements StreamObserver<OpenResponse> {
    private final ArgusLogger log = new ArgusLogger(ConnectObserver.class);
    private final ObserverListener listener;

    public ConnectObserver(ObserverListener listener) {
        this.listener = listener;
    }

    @Override
    public void onNext(OpenResponse stream) {
        try {
            var any = stream.getData();
            if (any.is(RequestData.class)) {
                listener.receive(any.unpack(RequestData.class));
            } else if (any.is(LoginData.class)) {
                listener.connected();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new StatusRuntimeException(Status.INTERNAL);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        log.debug("Failed to connect Central Service.");
        listener.error(throwable);
    }

    @Override
    public void onCompleted() {
        log.debug("Central Service shutdown after completed.");
        listener.completed();
    }

}
