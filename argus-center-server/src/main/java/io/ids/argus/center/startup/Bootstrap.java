package io.ids.argus.center.startup;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.grpc.ServerInterceptors;
import io.grpc.ServerServiceDefinition;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.netty.shaded.io.netty.channel.epoll.Epoll;
import io.grpc.netty.shaded.io.netty.channel.epoll.EpollEventLoopGroup;
import io.grpc.netty.shaded.io.netty.channel.epoll.EpollServerSocketChannel;
import io.grpc.netty.shaded.io.netty.channel.nio.NioEventLoopGroup;
import io.grpc.netty.shaded.io.netty.channel.socket.nio.NioServerSocketChannel;
import io.grpc.netty.shaded.io.netty.handler.ssl.ClientAuth;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslProvider;
import io.ids.argus.center.conf.CenterProperties;
import io.ids.argus.center.interceptor.DataFetchInterceptor;
import io.ids.argus.center.interceptor.ServiceInterceptor;
import io.ids.argus.center.module.ModuleManager;
import io.ids.argus.center.service.ConnectService;
import io.ids.argus.center.service.FetchArgsService;
import io.ids.argus.core.conf.ArgusLogger;
import io.ids.argus.core.enviroment.ArgusScanner;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

class Bootstrap {
    public final ArgusLogger log = new ArgusLogger(Bootstrap.class);
    private final io.grpc.Server server;
    private final ArgusScanner scanner = new ArgusScanner();

    Bootstrap() {
        List<ServerServiceDefinition> list = new ArrayList<>();
        list.add(ServerInterceptors.intercept(
                new ConnectService(),
                new ServiceInterceptor()));
        list.add(ServerInterceptors.intercept(
                new FetchArgsService(),
                new DataFetchInterceptor()));
        var boosGroup = Epoll.isAvailable() ?
                new EpollEventLoopGroup(1, new ThreadFactoryBuilder().build()) :
                new NioEventLoopGroup(Math.max(4, 2 * Runtime.getRuntime().availableProcessors()));
        var workGroup = Epoll.isAvailable() ?
                new EpollEventLoopGroup(1, new ThreadFactoryBuilder().build()) :
                new NioEventLoopGroup(Math.max(4, 2 * Runtime.getRuntime().availableProcessors()));
        var channelType = Epoll.isAvailable() ?
                EpollServerSocketChannel.class :
                NioServerSocketChannel.class;
        server = NettyServerBuilder
                .forPort(CenterProperties.get().getPort())
                .sslContext(getSslContext())
                .permitKeepAliveWithoutCalls(true)
                .permitKeepAliveTime(5, TimeUnit.SECONDS)
                .maxConnectionIdle(10L, TimeUnit.SECONDS)
                .bossEventLoopGroup(boosGroup)
                .workerEventLoopGroup(workGroup)
                .channelType(channelType)
                .addServices(list)
                .build();
    }

    public void start(Class<?> primarySource) throws IOException,
            URISyntaxException {
        scanner.scan(primarySource);
        String[] modules = CenterProperties.get().getModules();
        ModuleManager.get().initModules(Arrays.stream(modules).toList());
        server.start();
        log.debug("启动完成");
    }

    public void awaitTermination() throws InterruptedException {
        server.awaitTermination();
    }

    private SslContext getSslContext() {
        try {
            var publicPath = Paths.get(CenterProperties.get().getPublicPath());
            var ca = Paths.get(CenterProperties.get().getCaPublic());
            var pkcs8 = Paths.get(CenterProperties.get().getPKcs8());
            var sslClientContextBuilder = SslContextBuilder.forServer(
                    publicPath.toFile(),
                    pkcs8.toFile());
            sslClientContextBuilder.trustManager(ca.toFile());
            sslClientContextBuilder.clientAuth(ClientAuth.REQUIRE);
            return GrpcSslContexts.configure(sslClientContextBuilder,
                    SslProvider.OPENSSL).build();
        } catch (SSLException e) {
            return null;
        }
    }
}
