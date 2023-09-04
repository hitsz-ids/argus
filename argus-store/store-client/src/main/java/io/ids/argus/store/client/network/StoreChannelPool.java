package io.ids.argus.store.client.network;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.ids.argus.store.transport.NetworkAddress;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class StoreChannelPool {
    private ManagedChannel channel;
    private NetworkAddress address;
    private static final StoreChannelPool instance = new StoreChannelPool();

    private StoreChannelPool() {
    }

    public static StoreChannelPool get() {
        return instance;
    }

    public void init() throws IOException {
        this.address = new StoreNetworkAddress();
    }

    public ManagedChannel getChannel() {
        if (Objects.isNull(channel) || channel.isTerminated() || channel.isShutdown()) {
            channel = NettyChannelBuilder.forAddress(address.getAddress())
                    .usePlaintext()
                    .keepAliveWithoutCalls(true)
                    .keepAliveTime(10, TimeUnit.SECONDS)
                    .keepAliveTimeout(5, TimeUnit.SECONDS)
                    .build();
        }
        return channel;
    }
}
