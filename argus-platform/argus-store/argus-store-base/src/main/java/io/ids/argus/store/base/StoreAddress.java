package io.ids.argus.store.base;

import io.grpc.netty.shaded.io.netty.channel.unix.DomainSocketAddress;
import io.ids.argus.grpc.base.AddressType;
import io.ids.argus.grpc.base.ArgusAddress;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * The Argus store address
 */
public class StoreAddress extends ArgusAddress {

    public StoreAddress() throws IOException {
        super();
    }

    @Override
    protected SocketAddress initInetAddress(int port) {
        return new InetSocketAddress(port);
    }

    @Override
    protected SocketAddress initDomainAddress(String path) {
        return new DomainSocketAddress(path);
    }

    @Override
    public AddressType getType() {
        return AddressType.INET;
    }

    @Override
    public int getPort() {
        return StoreProperties.get().getPort();
    }

    @Override
    public String getFD() {
        return StoreProperties.get().getFD();
    }

}
