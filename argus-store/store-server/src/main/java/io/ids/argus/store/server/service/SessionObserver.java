package io.ids.argus.store.server.service;

import com.google.protobuf.Any;
import io.grpc.stub.StreamObserver;
import io.ids.argus.store.server.session.ArgusSession;
import io.ids.argus.store.server.session.SessionFactory;
import io.ids.argus.store.server.session.SessionManager;
import io.ids.argus.store.transport.grpc.*;

public class SessionObserver implements StreamObserver<OpenRequest> {

    private final StreamObserver<OpenResponse> sender;
    private final String id;
    private final ArgusSession<?> session;
    private final Object closedLock = new Object();
    private boolean closed = false;

    public SessionObserver(String id, SessionType type, StreamObserver<OpenResponse> sender) {
        this.id = id;
        this.sender = sender;
        session = SessionFactory.create(type);
        SessionManager.get().add(id, session);
    }

    @Override
    public void onNext(OpenRequest openRequest) {
        var storeResponse = OpenResponse.newBuilder()
                .setRequestId(openRequest.getRequestId());
        var any = openRequest.getData();
        if (any.is(Commit.class)) {
            session.commit();
            sender.onNext(storeResponse.build());
        } else if (any.is(Rollback.class)) {
            session.rollback();
            sender.onNext(storeResponse.build());
        } else if (any.is(RequestSessionMsg.class)) {
            sender.onNext(storeResponse.setData(Any.pack(
                            ResponseSessionMsg
                                    .newBuilder()
                                    .setSessionId(id)
                                    .build()))
                    .build());
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

    public String getId() {
        return id;
    }

    public void close() {
        synchronized (closedLock) {
            if (closed) {
                return;
            }
            closed = true;
        }
        session.close();
    }
}
