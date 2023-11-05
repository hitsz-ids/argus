package io.ids.argus.job.client.observer;

import com.google.protobuf.Any;
import io.grpc.stub.StreamObserver;
import io.ids.argus.core.conf.log.ArgusLogger;
import io.ids.argus.job.grpc.*;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class Connector implements StreamObserver<JobOpenResponse> {
    private final ArgusLogger log = new ArgusLogger(Connector.class);
    private final CountDownLatch latch = new CountDownLatch(1);
    private final ConnectorListener listener;
    private StreamObserver<JobOpenRequest> requester;
    private Throwable error;

    public Connector(ConnectorListener listener) {
        this.listener = listener;
    }

    public void setRequester(StreamObserver<JobOpenRequest> requester) {
        this.requester = requester;
    }

    @Override
    public void onNext(JobOpenResponse jobOpenResponse) {
        var data = jobOpenResponse.getData();
        if (data.is(ConnectData.class)) {
            latch.countDown();
            log.debug("ArgusJob service connect success.");
        } else if (data.is(State.class)) {
            try {
                var message = data.unpack(State.class);
                listener.receive(message.getSeq(),
                        message.getData());
                requester.onNext(JobOpenRequest.newBuilder()
                        .setData(Any.pack(AckData.newBuilder()
                                .setRequestId(message.getRequestId())
                                .build()))
                        .build());
            } catch (Exception e) {
                //todo 消息处理失败
            }
        }
    }

    @Override
    public void onError(Throwable throwable) {
        error = throwable;
        latch.countDown();
        listener.reconnect();
    }

    @Override
    public void onCompleted() {
        latch.countDown();
        listener.reconnect();
    }

    public void await() throws InterruptedException {
        latch.await();
    }

    public boolean isReady() {
        return Objects.isNull(error);
    }
}
