package io.ids.argus.center.service;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.ids.argus.center.common.ExecuteStatus;
import io.ids.argus.center.startup.Command;
import io.ids.argus.center.exception.ArgusExecuteException;
import io.ids.argus.center.module.ModuleManager;
import io.ids.argus.center.module.LockPool;
import io.ids.argus.core.base.conf.ArgusLogger;
import io.ids.argus.core.transport.grpc.ArgusModule;
import io.ids.argus.core.transport.grpc.OpenRequest;
import io.ids.argus.core.transport.grpc.OpenResponse;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Connector extends Thread implements StreamObserver<OpenRequest> {
    private final ArgusLogger log = new ArgusLogger(Connector.class);
    private Status status = Status.UNKNOWN;
    private final BlockingQueue<Command> commands = new LinkedBlockingQueue<>();
    private final ArgusModule module;
    private final StreamObserver<OpenResponse> observer;
    private boolean stop = false;

    public Connector(ArgusModule module,
                     StreamObserver<OpenResponse> observer) {
        this.module = module;
        this.observer = observer;
    }

    @Override
    public void onNext(OpenRequest stream) {
        LockPool.get().lock(module);
        try {
            var module = stream.getModule();
            if (!Objects.equals(status, Status.OK) && module.equals(this.module)) {
                status = Status.OK;
                ModuleManager.get().login(this.module, this);
                var serverStream = OpenResponse.newBuilder()
                        .setLogin(OpenResponse.Login.newBuilder()
                                .setModule(module)
                                .build())
                        .build();
                observer.onNext(serverStream);
                start();
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
            logout();
            log.debug("模块[{}:version】，已退出登录",
                    module.getName(),
                    module.getVersion());
            log.error(throwable.getMessage(), throwable);
        } finally {
            LockPool.get().unlock(module);
        }
    }

    @Override
    public void onCompleted() {
        LockPool.get().lock(module);
        try {
            logout();
            log.debug("模块[{}:{}]，已退出登录",
                    module.getName(),
                    module.getVersion());
        } finally {
            LockPool.get().unlock(module);
        }

    }

    private void logout() {
        ModuleManager.get().logout(module);
//        if (listener != null) {
//            listener.disconnect(pluginName);
//        }
//        listener = null;
        stop = true;
        commands.clear();
        Thread.currentThread().interrupt();
    }

    public boolean ok() {
        return Objects.equals(status, Status.OK);
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                var command = commands.take();
                var type = OpenResponse.Type.NOOP;
                if (command.getType() == Command.Type.FETCH) {
                    type = OpenResponse.Type.FETCH;
                }
                observer.onNext(OpenResponse.newBuilder()
                        .setNotice(OpenResponse.Notice.newBuilder()
                                .setRequestId(command.getUuid())
                                .setUrl(command.getUrl())
                                .setType(type)
                                .build())
                        .build());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error(e.getMessage(), e);
            }
        }
    }

    public ArgusModule getModule() {
        return module;
    }

    public boolean produce(Command command) {
        try {
            return commands.offer(command, 5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ArgusExecuteException(ExecuteStatus.EXECUTE_COMMAND_FAILED);
        }
    }
}
