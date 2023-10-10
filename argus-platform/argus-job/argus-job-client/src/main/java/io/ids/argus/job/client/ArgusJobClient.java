package io.ids.argus.job.client;

import com.google.protobuf.Any;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import io.grpc.stub.StreamObserver;
import io.ids.argus.core.conf.log.ArgusLogger;
import io.ids.argus.grpc.base.ArgusGrpcClient;
import io.ids.argus.job.base.ConnectorId;
import io.ids.argus.job.base.JobAddress;
import io.ids.argus.job.client.observer.Connector;
import io.ids.argus.job.client.observer.ConnectorListener;
import io.ids.argus.job.grpc.*;

import java.util.concurrent.TimeUnit;

class ArgusJobClient extends ArgusGrpcClient implements ConnectorListener {
    private static final ArgusLogger log = new ArgusLogger(ArgusJobClient.class);
    private final Connector connector;
    private final JobExecutor executor;
    private final ConnectorId connectorId;

    ArgusJobClient(JobAddress address, ConnectorId connectorId) {
        super(address);
        this.connectorId = connectorId;
        this.connector = new Connector(this);
        this.executor = new JobExecutor(this);
        start();
    }

    public void start() {
        connect();
        try {
            connector.await();
        } catch (InterruptedException e) {
            System.exit(-1);
        }
        recovery();
    }

    void connect() {
        var stub = JobServiceGrpc.newStub(getChannel());
        StreamObserver<JobOpenRequest> requester = stub
                .withInterceptors(MetadataUtils.newAttachHeadersInterceptor(createHeader()))
                .open(connector);
        requester.onNext(JobOpenRequest.newBuilder()
                .setData(Any.pack(ConnectData.newBuilder()
                        .build()))
                .build());
        connector.setRequester(requester);
    }

    @Override
    public void reconnect() {
        log.debug("ArgusJob服务连接失败, 准备发起重连");
        try {
            TimeUnit.SECONDS.sleep(8);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        connect();
    }

    @Override
    public boolean receive(String seq, Any any) throws Exception {
        return executor.receive(seq, any);
    }

    public JobCommitResponse commit(JobCommitRequest request) {
        return executor.commit(request);
    }

    public JobStopResponse stop(String seq) {
        return executor.stop(seq);
    }

    @Override
    public Metadata createHeader() {
        var header = new Metadata();
        header.put(GrpcConstants.VERSION_HEADER, connectorId.version());
        header.put(GrpcConstants.MODULE_HEADER, connectorId.module());
        return header;
    }

    public void recovery() {
        executor.recovery();
    }
}
