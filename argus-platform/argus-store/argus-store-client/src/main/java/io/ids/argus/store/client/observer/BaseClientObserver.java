package io.ids.argus.store.client.observer;

import io.grpc.Metadata;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import io.ids.argus.core.conf.log.ArgusLogger;
import io.ids.argus.store.base.StorageUtils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public abstract class BaseClientObserver<C,S> implements StreamObserver<S> {
    private static final ArgusLogger log = new ArgusLogger(StorageUtils.class);
    protected StreamObserver<C> sender;
    protected Exception serverException;
    protected boolean closeSession = false;
    private final ReentrantLock closedLock = new ReentrantLock();
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    public void setSender(StreamObserver<C> sender){
        this.sender = sender;
    }

    @Override
    public void onError(Throwable throwable) {
        try {
            Metadata metadata = ((StatusRuntimeException) throwable).getTrailers();
            if (metadata != null) {
                byte[] bytes = metadata.get(Metadata.Key.of("exception-bin", Metadata.BINARY_BYTE_MARSHALLER));
                byte[] errorBytes = metadata.get(Metadata.Key.of("exception-message-bin", Metadata.BINARY_BYTE_MARSHALLER));
                if (bytes != null) {
                    serverException = new Exception(StorageUtils.toThrowable(
                            bytes,
                            errorBytes));
                } else {
                    serverException = new Exception(throwable);
                }
            } else {
                serverException = new Exception(throwable);
            }
        } finally {
            sender.onCompleted();
            destroy();
        }
        log.debug("[storage]：------> request finish-onError!");
    }

    @Override
    public void onCompleted() {
        sender.onCompleted();
    }
    public abstract void destroy();

    public void close() {
        synchronized (closedLock) {
            if (closeSession) {
                return;
            }
            closeSession = true;
        }
    }
    protected void signal(){
        condition.signal();
    }
    protected boolean await() throws InterruptedException {
        return condition.await(60, TimeUnit.SECONDS);
    }
    protected void lock(){
        lock.lock();
    }
    protected void unlock(){
        lock.unlock();
    }
}
