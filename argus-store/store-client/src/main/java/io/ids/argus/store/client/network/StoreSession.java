package io.ids.argus.store.client.network;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.Channel;
import io.grpc.ClientInterceptor;
import io.grpc.Metadata;
import io.grpc.stub.AbstractStub;
import io.grpc.stub.MetadataUtils;
import io.grpc.stub.StreamObserver;
import io.ids.argus.core.base.utils.Utils;
import io.ids.argus.store.client.common.ErrorStatus;
import io.ids.argus.store.client.exception.ArgusStoreException;
import io.ids.argus.store.client.session.JobSession;
import io.ids.argus.store.transport.GrpcUtils;
import io.ids.argus.store.transport.grpc.*;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public abstract class StoreSession<T extends AbstractStub<T>> implements AutoCloseable,
        StreamObserver<OpenResponse> {
    private final StreamObserver<OpenRequest> sender;
    private final Object closedLock = new Object();
    private boolean closed = false;
    private final Map<String, Message> map = new ConcurrentHashMap<>();
    private final String sessionId;
    protected T stub;

    private static class Message {
        final CountDownLatch latch = new CountDownLatch(1);
        final String uuid = UUID.randomUUID().toString();
        Any response;
    }

    protected StoreSession() throws InvalidProtocolBufferException {
        var channel = StoreChannelPool.get().getChannel();
        sender = SessionServiceGrpc.newStub(channel)
                .withInterceptors(createSessionHeader())
                .open(this);
        Any response = send(RequestSessionMsg.newBuilder().build());
        sessionId = response.unpack(ResponseSessionMsg.class).getSessionId();
        stub = getStub(channel)
                .withInterceptors(createRequestHeader());
    }

    private ClientInterceptor createRequestHeader() {
        var header = new Metadata();
        Metadata.Key<String> requestId =
                Metadata.Key.of(GrpcUtils.HEADER_REQUEST_ID,
                        Metadata.ASCII_STRING_MARSHALLER);
        header.put(requestId, sessionId);
        return MetadataUtils.newAttachHeadersInterceptor(header);
    }

    private ClientInterceptor createSessionHeader() {
        var header = new Metadata();
        Metadata.Key<byte[]> typeHeader =
                Metadata.Key.of(GrpcUtils.HEADER_SESSION_TYPE,
                        Metadata.BINARY_BYTE_MARSHALLER);
        header.put(typeHeader, Utils.intToBytes(getType().getNumber()));
        return MetadataUtils.newAttachHeadersInterceptor(header);
    }

    protected abstract T getStub(Channel channel);

    public void commit() {
        if (isClosed()) {
            return;
        }
        send(Commit.newBuilder().build());
    }

    public void rollback() {
        if (isClosed()) {
            return;
        }
        send(Rollback.newBuilder().build());
    }

    private <M extends com.google.protobuf.Message> Any send(M message) {
        var msg = new Message();
        map.put(msg.uuid, msg);
        sender.onNext(OpenRequest.newBuilder()
                .setRequestId(msg.uuid)
                .setData(Any.pack(message))
                .build());
        boolean pass;
        try {
            pass = msg.latch.await(60, TimeUnit.SECONDS);
            if (!pass) {
                throw new ArgusStoreException(ErrorStatus.TIME_OUT);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ArgusStoreException(ErrorStatus.ERROR);
        }
        return msg.response;
    }

    @Override
    public void close() {
        synchronized (closedLock) {
            if (closed) {
                return;
            }
            closed = true;
        }
        sender.onCompleted();
    }

    private boolean isClosed() {
        return closed;
    }

    @Override
    public void onNext(OpenResponse openResponse) {
        var message = map.remove(openResponse.getRequestId());
        if (!Objects.isNull(message)) {
            message.response = openResponse.getData();
            message.latch.countDown();
        }
    }

    @Override
    public void onError(Throwable throwable) {
        close();
    }

    @Override
    public void onCompleted() {
        close();
    }

    protected abstract SessionType getType();
}
