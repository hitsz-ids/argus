package io.ids.argus.server.base;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.grpc.ServerInterceptors;
import io.grpc.ServerServiceDefinition;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.netty.shaded.io.netty.channel.epoll.Epoll;
import io.grpc.netty.shaded.io.netty.channel.epoll.EpollEventLoopGroup;
import io.grpc.netty.shaded.io.netty.channel.epoll.EpollServerSocketChannel;
import io.grpc.netty.shaded.io.netty.channel.nio.NioEventLoopGroup;
import io.grpc.netty.shaded.io.netty.channel.socket.nio.NioServerSocketChannel;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.ids.argus.server.base.interceptor.FetchServiceInterceptor;
import io.ids.argus.server.base.interceptor.ServiceInterceptor;
import io.ids.argus.server.base.module.configuration.ModuleConfiguration;
import io.ids.argus.server.base.service.ConnectService;
import io.ids.argus.server.base.service.FetchArgsService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ArgusServer {
    private final io.grpc.Server server;

    public ArgusServer(int port,
                       SslContext sslContext,
                       String[] modules,
                       String modulePubDir) {
        var configuration = new ModuleConfiguration(modulePubDir);
        List<ServerServiceDefinition> list = new ArrayList<>();
        list.add(ServerInterceptors.intercept(
                new ConnectService(),
                new ServiceInterceptor(configuration)));
        list.add(ServerInterceptors.intercept(
                new FetchArgsService(),
                new FetchServiceInterceptor(configuration)));
        var boosGroup = Epoll.isAvailable() ?
                new EpollEventLoopGroup(1, new ThreadFactoryBuilder().build()) :
                new NioEventLoopGroup(Math.max(4, 2 * Runtime.getRuntime().availableProcessors()));
        var workGroup = Epoll.isAvailable() ?
                new EpollEventLoopGroup(1, new ThreadFactoryBuilder().build()) :
                new NioEventLoopGroup(Math.max(4, 2 * Runtime.getRuntime().availableProcessors()));
        var channelType = Epoll.isAvailable() ?
                EpollServerSocketChannel.class :
                NioServerSocketChannel.class;
        var serverBuilder = NettyServerBuilder
                .forPort(port)
                .sslContext(sslContext)
                .permitKeepAliveWithoutCalls(true)
                .permitKeepAliveTime(5, TimeUnit.SECONDS)
                .maxConnectionIdle(10L, TimeUnit.SECONDS)
                .bossEventLoopGroup(boosGroup)
                .workerEventLoopGroup(workGroup)
                .channelType(channelType);
        configuration.initModules(modules);
        server = serverBuilder
                .addServices(list)
                .build();
    }

    public void start() throws IOException {
        server.start();
    }

    public void awaitTermination() throws InterruptedException {
        server.awaitTermination();
    }
}
