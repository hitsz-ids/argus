package io.ids.argus.module;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NegotiationType;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.ids.argus.module.conf.ModuleProperties;

import javax.net.ssl.SSLException;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class Channel {
    private ManagedChannel channel;
    private static final Channel instance = new Channel();
    public static Channel get() {
        return instance;
    }
    private SslContext buildSslContext() throws SSLException {
        var builder = GrpcSslContexts.forClient();
        var publicPath = Paths.get(ModuleProperties.get().getPublicPath());
        var ca = Paths.get(ModuleProperties.get().getCaPublic());
        var pkcs8 = Paths.get(ModuleProperties.get().getPKcs8());
        builder.trustManager(ca.toFile());
        builder.keyManager(publicPath.toFile(), pkcs8.toFile());
        return builder.build();
    }

    public ManagedChannel getChannel() throws SSLException {
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
        return channel;
    }
}
