package io.ids.argus.module;

import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import io.ids.argus.core.base.exception.ArgusScannerException;
import io.ids.argus.core.base.json.Transformer;
import io.ids.argus.core.base.utils.Security;
import io.ids.argus.core.conf.log.ArgusLogger;
import io.ids.argus.core.grpc.*;
import io.ids.argus.job.base.ConnectorId;
import io.ids.argus.job.client.ArgusJob;
import io.ids.argus.job.client.job.JobEntity;
import io.ids.argus.job.grpc.JobCommitRequest;
import io.ids.argus.job.grpc.JobCommitResponse;
import io.ids.argus.module.conf.ModuleProperties;
import io.ids.argus.module.context.ModuleContext;
import io.ids.argus.module.observer.ConnectObserver;
import io.ids.argus.module.observer.FetchObserver;
import io.ids.argus.module.observer.ObserverListener;
import io.ids.argus.module.utils.ModuleConstants;
import io.ids.argus.server.grpc.GrpcConstants;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.TimeUnit;

public class ArgusModule implements ObserverListener {
    private final ArgusLogger log = new ArgusLogger(ArgusModule.class);
    private final ConnectObserver observer;
    private final String name;
    private final String version;
    private final ModuleContext context;
    private static final int RECONNECT_TIME_OUT = 10;
    private static final ArgusModule instance = new ArgusModule();

    public static ArgusModule get() {
        return instance;
    }

    private ArgusModule() {
        context = new ModuleContext();
        name = ModuleProperties.get().getName();
        version = ModuleProperties.get().getVersion();
        observer = new ConnectObserver(this);
        ArgusJob.init(new ConnectorId(name, version));
    }

    public static void start(Class<?> primarySource) throws ArgusScannerException {
        instance.context.scan(primarySource);
        instance.connect();
    }

    private void fetch(String id) {
        try {
            var header = new Metadata();
            header.put(GrpcConstants.HEADER_REQUEST_ID, id);
            FetchServiceGrpc.FetchServiceStub fetch = FetchServiceGrpc.newStub(Channel.get().getChannel())
                    .withInterceptors(MetadataUtils.newAttachHeadersInterceptor(header))
                    .withDeadlineAfter(ModuleConstants.FETCH_TIME_OUT, TimeUnit.SECONDS);
            var fetchObserver = new FetchObserver(context);
            var server = fetch.fetch(fetchObserver);
            fetchObserver.setServer(server);
        } catch (SSLException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Metadata createHeader() {
        var header = new Metadata();
        try {
            String signData =
                    Security.sign(ModuleProperties.get().getPKcs8(), name);
            header.put(GrpcConstants.HEADER_MODULE_VERSION, version);
            header.put(GrpcConstants.HEADER_MODULE_NAME_SECRET, signData);
        } catch (InvalidKeySpecException | SignatureException | NoSuchAlgorithmException | InvalidKeyException |
                 IOException e) {
            log.error(e.getMessage(), e);
        }
        return header;
    }

    @Override
    public void connected() {
        log.debug("已经成功登录到中心服务");
    }

    @Override
    public void receive(RequestData data) {
        fetch(data.getRequestId());
    }

    @Override
    public void error(Throwable throwable) {
        log.error(throwable.getMessage(), throwable);
        reconnect();
    }

    @Override
    public void completed() {
        observer.onCompleted();
        reconnect();
    }

    private void reconnect() {
        log.debug("向中心发起重新登录请求");
        try {
            TimeUnit.SECONDS.sleep(RECONNECT_TIME_OUT);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        connect();
    }

    private void connect() {
        try {
            ArgusServiceGrpc.ArgusServiceStub stub = ArgusServiceGrpc.newStub(Channel.get().getChannel())
                    .withInterceptors(MetadataUtils.newAttachHeadersInterceptor(createHeader()));
            stub.open(observer).onNext(OpenRequest.newBuilder()
                    .setModule(ArgusModuleData.newBuilder()
                            .setName(name)
                            .setVersion(version)
                            .build())
                    .build());
        } catch (SSLException e) {
            log.error(e.getMessage(), e);
        }
    }

    public String commit(JobEntity<?, ?> job) {
        var res = ArgusJob.get().commit(JobCommitRequest.newBuilder()
                .setName(job.getName())
                .setParams(Transformer.toJsonString(job.getParams()))
                .setJob(job.getJob().getName())
                .build());
        return res.getSeq();
    }
}
