package io.ids.argus.server.base.service;

import com.google.protobuf.Any;
import com.google.protobuf.Message;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.ids.argus.core.conf.log.ArgusLogger;
import io.ids.argus.core.grpc.LoginData;
import io.ids.argus.core.grpc.OpenRequest;
import io.ids.argus.core.grpc.OpenResponse;
import io.ids.argus.server.base.module.manager.Connector;
import io.ids.argus.server.base.module.manager.ArgusModule;
import io.ids.argus.server.base.module.manager.ArgusModuleManager;

import java.util.Objects;

public class ConnectObserver implements StreamObserver<OpenRequest>, Connector {
    private final ArgusLogger log = new ArgusLogger(ConnectObserver.class);
    private final ArgusModule module;
    private final StreamObserver<OpenResponse> observer;
    private Status status;

    public ConnectObserver(ArgusModule module, StreamObserver<OpenResponse> observer) {
        this.module = module;
        this.observer = observer;
        this.module.bind(this);
        ArgusModuleManager.get().login(this.module);
    }

    @Override
    public void onNext(OpenRequest stream) {
        LockPool.get().lock(module);
        try {
            var module = stream.getModule();
            if (!Objects.equals(status, Status.OK) &&
                    Objects.equals(module.getName(), this.module.getName()) &&
                    Objects.equals(module.getVersion(), this.module.getVersion())) {
                status = Status.OK;
                observer.onNext(OpenResponse.newBuilder()
                        .setData(Any.pack(LoginData.newBuilder().build()))
                        .build());
                log.debug("模块[{}:{}]，已成功登录",
                        module.getName(),
                        module.getVersion());
            }
        } finally {
            LockPool.get().unlock(module);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        LockPool.get().lock(module);
        try {
            ArgusModuleManager.get().logout(module);
            log.debug("模块[{}:{}]，已退出登录",
                    module.getName(),
                    module.getVersion());
        } finally {
            LockPool.get().unlock(module);
        }
    }

    @Override
    public void onCompleted() {
        LockPool.get().lock(module);
        try {
            ArgusModuleManager.get().logout(module);
            log.debug("模块[{}:{}]，已退出登录",
                    module.getName(),
                    module.getVersion());
        } finally {
            LockPool.get().unlock(module);
        }
    }

    @Override
    public boolean isReady() {
        return status.isOk();
    }

    @Override
    public void call(Message message) {
        observer.onNext(OpenResponse.newBuilder()
                .setData(Any.pack(message))
                .build());
    }
}
