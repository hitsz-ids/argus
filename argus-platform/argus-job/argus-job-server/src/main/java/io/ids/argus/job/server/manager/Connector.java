package io.ids.argus.job.server.manager;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.ids.argus.job.base.ConnectorId;
import io.ids.argus.job.grpc.*;
import io.ids.argus.job.server.error.CallbackError;
import io.ids.argus.job.server.exception.ArgusJobServerCallbackException;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

class Connector implements StreamObserver<JobOpenRequest> {
    private record InternalMessage(CountDownLatch latch) {
    }

    private final StreamObserver<JobOpenResponse> observer;

    private final ConnectorId id;
    private final Map<String, InternalMessage> messages = new ConcurrentHashMap<>();

    Connector(StreamObserver<JobOpenResponse> observer, ConnectorId id) {
        //todo need verify module
        this.observer = observer;
        this.id = id;
    }

    @Override
    public void onNext(JobOpenRequest jobOpenRequest) {
        var data = jobOpenRequest.getData();
        try {
            if (data.is(ConnectData.class)) {
                observer.onNext(JobOpenResponse.newBuilder()
                        .setData(Any.pack(ConnectData.newBuilder().build()))
                        .build());
                ConnectorManager.get().bind(this);
            } else if (data.is(AckData.class)) {
                AckData ackData;
                ackData = data.unpack(AckData.class);
                var iMessage = messages.remove(ackData.getRequestId());
                if (!Objects.isNull(iMessage)) {
                    iMessage.latch.countDown();
                }
            }
        } catch (InvalidProtocolBufferException e) {
            //todo protocol buf 解析错误处理
            observer.onError(Status.INTERNAL.withDescription("parse protocol error").asException());
        }
    }

    @Override
    public void onError(Throwable throwable) {
        ConnectorManager.get().unBind(this);
    }

    @Override
    public void onCompleted() {
        ConnectorManager.get().unBind(this);
    }

    public void callback(String seq,
                         com.google.protobuf.Message messageData) throws ArgusJobServerCallbackException {
        var iMessage = new InternalMessage(new CountDownLatch(1));
        var requestId = UUID.randomUUID().toString();
        messages.put(requestId, iMessage);
        var state = State.newBuilder()
                .setSeq(seq)
                .setRequestId(requestId)
                .setData(Any.pack(messageData))
                .build();
        observer.onNext(JobOpenResponse.newBuilder()
                .setData(Any.pack(state))
                .build());
        try {
            var pass = iMessage.latch.await(10, TimeUnit.SECONDS);
            if (!pass) {
                throw new ArgusJobServerCallbackException(CallbackError.ERROR_CALLBACK_TIME_OUT);
            }
        } catch (Exception e) {
            throw new ArgusJobServerCallbackException(CallbackError.ERROR_CALLBACK);
        }
    }

    public ConnectorId getId() {
        return id;
    }
}
