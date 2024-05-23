package io.ids.argus.store.server.service;

import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.ids.argus.core.conf.log.ArgusLogger;
import io.ids.argus.store.grpc.file.UploadRequest;
import org.apache.commons.lang3.SerializationUtils;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.locks.ReentrantLock;


public abstract class BaseServerObserver<T,V> implements StreamObserver<V> {
    private static final ArgusLogger log = new ArgusLogger(BaseServerObserver.class);
    private final ReentrantLock lock = new ReentrantLock();
    private boolean destroyed = false;

    protected final StreamObserver<T> pusher;
    protected String fileName;

    protected BaseServerObserver(StreamObserver<T> pusher) {
        this.pusher = pusher;
    }


    public void exception(Throwable throwable) {
        exception(throwable, true);
    }

    public void exception(Throwable throwable, boolean finish) {
        Metadata metadata = new Metadata();
        Metadata.Key<byte[]> header = Metadata.Key.of("exception-bin", Metadata.BINARY_BYTE_MARSHALLER);
        Metadata.Key<byte[]> messageHeader = Metadata.Key.of("exception-message-bin", Metadata.BINARY_BYTE_MARSHALLER);
        metadata.put(header, SerializationUtils.serialize(throwable));
        throwable.printStackTrace();
        metadata.put(messageHeader,
                (throwable.getClass().getName() + ":" + throwable).getBytes(StandardCharsets.UTF_8));
        pusher.onError(Status.INTERNAL.asRuntimeException(metadata));
        if (finish) {
            finish(false, true);
        }
    }

    public abstract void onNext(V request);

    @Override
    public void onError(Throwable throwable) {
        finish(true, true);
    }

    public abstract void onCompleted();

    public void finish(boolean completed, boolean error) {
        lock.lock();
        try {
            if (destroyed) {
                return;
            }
            destroyed = true;
            if (completed) {
                pusher.onCompleted();
            }
        } finally {
            lock.unlock();
            log.debug("[storage]：------> finish!");
        }
    }
}