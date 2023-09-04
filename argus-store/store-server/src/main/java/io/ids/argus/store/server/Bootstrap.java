package io.ids.argus.store.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerInterceptors;
import io.grpc.ServerServiceDefinition;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.netty.shaded.io.netty.channel.epoll.Epoll;
import io.grpc.netty.shaded.io.netty.channel.epoll.EpollEventLoopGroup;
import io.grpc.netty.shaded.io.netty.channel.epoll.EpollServerSocketChannel;
import io.grpc.netty.shaded.io.netty.channel.nio.NioEventLoopGroup;
import io.grpc.netty.shaded.io.netty.channel.socket.nio.NioServerSocketChannel;
import io.ids.argus.store.server.db.conf.DbInstance;
import io.ids.argus.store.server.interceptor.RequestInterceptor;
import io.ids.argus.store.server.interceptor.SessionInterceptor;
import io.ids.argus.store.server.service.SessionService;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Bootstrap {
    private final NettyServerBuilder serverBuilder;
    private Server server;

    private final List<ServerServiceDefinition> list = new ArrayList<>();

    Bootstrap() {
        list.add(ServerInterceptors.intercept(new SessionService(),
                new SessionInterceptor()));
        var boosGroup = Epoll.isAvailable() ?
                new EpollEventLoopGroup(1, new ThreadFactoryBuilder().build()) :
                new NioEventLoopGroup(Math.max(4, 2 * Runtime.getRuntime().availableProcessors()));
        var workGroup = Epoll.isAvailable() ?
                new EpollEventLoopGroup(1, new ThreadFactoryBuilder().build()) :
                new NioEventLoopGroup(Math.max(4, 2 * Runtime.getRuntime().availableProcessors()));
        var channelType = Epoll.isAvailable() ?
                EpollServerSocketChannel.class :
                NioServerSocketChannel.class;
        serverBuilder = NettyServerBuilder
                .forPort(5555)
                .permitKeepAliveWithoutCalls(true)
                .permitKeepAliveTime(5, TimeUnit.SECONDS)
                .maxConnectionIdle(10L, TimeUnit.SECONDS)
                .bossEventLoopGroup(boosGroup)
                .workerEventLoopGroup(workGroup)
                .channelType(channelType);
        try {
            DbInstance.get().initDb();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void start() throws IOException, InterruptedException {
        serverBuilder.addServices(list);
        server = serverBuilder.build();
        server.start();
        server.awaitTermination();
    }

    public void shutdown() {
        server.shutdown();
    }

    public void addService(Class<? extends BindableService> serviceClass) {
        try {
            list.add(ServerInterceptors.intercept(
                    serviceClass.getConstructor().newInstance(),
                    new RequestInterceptor()));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }
}
