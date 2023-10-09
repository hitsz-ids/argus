package io.ids.argus.grpc.base;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.grpc.Server;
import io.grpc.ServerServiceDefinition;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.netty.shaded.io.netty.channel.epoll.Epoll;
import io.grpc.netty.shaded.io.netty.channel.epoll.EpollEventLoopGroup;
import io.grpc.netty.shaded.io.netty.channel.epoll.EpollServerSocketChannel;
import io.grpc.netty.shaded.io.netty.channel.nio.NioEventLoopGroup;
import io.grpc.netty.shaded.io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class ArgusGrpcServer {
    private final Server server;

    protected ArgusGrpcServer(ArgusAddress address) {
        var list = new ArrayList<ServerServiceDefinition>();
        addServices(list);
        server = createServer(address)
                .addServices(list)
                .build();
    }

    public void start() throws IOException {
        server.start();
    }

    public void awaitTermination() throws InterruptedException {
        server.awaitTermination();
    }

    public void shutdown() {
        server.shutdown();
    }

    protected abstract void addServices(List<ServerServiceDefinition> list);

    private NettyServerBuilder createServer(ArgusAddress address) {
        var boosGroup = Epoll.isAvailable() ?
                new EpollEventLoopGroup(1, new ThreadFactoryBuilder().build()) :
                new NioEventLoopGroup(Math.max(4, 2 * Runtime.getRuntime().availableProcessors()));
        var workGroup = Epoll.isAvailable() ?
                new EpollEventLoopGroup(1, new ThreadFactoryBuilder().build()) :
                new NioEventLoopGroup(Math.max(4, 2 * Runtime.getRuntime().availableProcessors()));
        var channelType = Epoll.isAvailable() ?
                EpollServerSocketChannel.class :
                NioServerSocketChannel.class;
        return NettyServerBuilder
                .forAddress(address.getAddress())
                .permitKeepAliveWithoutCalls(true)
                .permitKeepAliveTime(5, TimeUnit.SECONDS)
                .maxConnectionIdle(10L, TimeUnit.SECONDS)
                .bossEventLoopGroup(boosGroup)
                .workerEventLoopGroup(workGroup)
                .channelType(channelType);
    }
}
