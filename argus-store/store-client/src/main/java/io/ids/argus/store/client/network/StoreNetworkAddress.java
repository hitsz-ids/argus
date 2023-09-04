package io.ids.argus.store.client.network;

import io.grpc.netty.shaded.io.netty.channel.epoll.Epoll;
import io.ids.argus.store.transport.NetworkAddress;
import io.ids.argus.store.transport.NetworkType;

import java.io.IOException;

public class StoreNetworkAddress extends NetworkAddress {
    public StoreNetworkAddress() throws IOException {
        super();
    }

    @Override
    public NetworkType getType() {
        return Epoll.isAvailable() ? NetworkType.DOMAIN : NetworkType.INET;
    }
}
