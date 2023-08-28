package io.ids.argus.module;

import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NegotiationType;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.grpc.stub.MetadataUtils;
import io.ids.argus.core.common.GrpcCommon;
import io.ids.argus.core.conf.ArgusLogger;
import io.ids.argus.core.exception.ArgusScannerException;
import io.ids.argus.core.grpc.*;
import io.ids.argus.core.utils.Security;
import io.ids.argus.module.conf.ModuleProperties;
import io.ids.argus.module.context.ModuleContext;
import io.ids.argus.module.observer.Connection;
import io.ids.argus.module.observer.FetchArgsObserver;
import io.ids.argus.module.observer.ObserverListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.TimeUnit;

public class ArgusClient implements ObserverListener {
    private final ArgusLogger log = new ArgusLogger(ArgusClient.class);
    private ManagedChannel channel;
    private ArgusServiceGrpc.ArgusServiceStub stub;
    private final Connection observer;
    private final ArgusModule module;

    private final ModuleContext context;
    private static final int RECONNECT_TIME_OUT = 10;

    public ArgusClient() {
        context = new ModuleContext();
        var name = ModuleProperties.get().getName();
        var version = ModuleProperties.get().getVersion();
        module = ArgusModule.newBuilder()
                .setName(name)
                .setVersion(version)
                .build();
        observer = new Connection(module, this);
    }

    public void start(Class<?> primarySource) throws SSLException, ArgusScannerException {
        context.scan(primarySource);
        createChannel();
        stub = ArgusServiceGrpc.newStub(channel)
                .withInterceptors(MetadataUtils.newAttachHeadersInterceptor(createHeader()));
        stub.open(observer).onNext(OpenRequest.newBuilder()
                .setModule(module)
                .build());
    }

    public void createChannel() throws SSLException {
        if (channel == null || channel.isShutdown() || channel.isTerminated()) {
            channel = NettyChannelBuilder.forAddress(ModuleProperties.get().getCenterHost(),
                            ModuleProperties.get().getCenterPort())
                    .negotiationType(NegotiationType.TLS)
                    .overrideAuthority("argus")
                    .sslContext(buildSslContext())
                    .keepAliveWithoutCalls(true)
                    .keepAliveTime(5, TimeUnit.SECONDS)
                    .build();
        }
    }

    private void fetch(String id) {
        try {
            createChannel();
            var header = new Metadata();
            Metadata.Key<String> requestId =
                    Metadata.Key.of(GrpcCommon.HEADER_REQUEST_ID, Metadata.ASCII_STRING_MARSHALLER);
            header.put(requestId, id);
            FetchServiceGrpc.FetchServiceStub fetch = FetchServiceGrpc.newStub(channel)
                    .withInterceptors(MetadataUtils.newAttachHeadersInterceptor(header));
            var fetchObserver = new FetchArgsObserver(context);
            var server = fetch.fetch(fetchObserver);
            fetchObserver.setServer(server);
        } catch (SSLException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Metadata createHeader() {
        var header = new Metadata();
        try {
            String signData = Security.sign(ModuleProperties.get().getPKcs8(), module.getName());
            Metadata.Key<String> name =
                    Metadata.Key.of(GrpcCommon.HEADER_MODULE_NAME, Metadata.ASCII_STRING_MARSHALLER);
            Metadata.Key<String> versionHeader = Metadata.Key.of(GrpcCommon.HEADER_MODULE_VERSION,
                    Metadata.ASCII_STRING_MARSHALLER);
            header.put(versionHeader, module.getVersion());
            header.put(name, signData);
        } catch (InvalidKeySpecException | SignatureException | NoSuchAlgorithmException | InvalidKeyException |
                 IOException e) {
            log.error(e.getMessage(), e);
        }
        return header;
    }

    private static SslContext buildSslContext() throws SSLException {
        var builder = GrpcSslContexts.forClient();
        var publicPath = Paths.get(ModuleProperties.get().getPublicPath());
        var ca = Paths.get(ModuleProperties.get().getCaPublic());
        var pkcs8 = Paths.get(ModuleProperties.get().getPKcs8());
        builder.trustManager(ca.toFile());
        builder.keyManager(publicPath.toFile(), pkcs8.toFile());
        return builder.build();
    }

    @Override
    public void receive(OpenResponse.Notice notice) {
        if (notice.getType() == OpenResponse.Type.FETCH) {
            fetch(notice.getRequestId());
        }
    }

    @Override
    public void reconnect() {
        try {
            TimeUnit.SECONDS.sleep(RECONNECT_TIME_OUT);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.debug("向中心发起重新登录请求");
        stub.open(observer).onNext(OpenRequest.newBuilder()
                .setModule(module)
                .build());
    }
}
