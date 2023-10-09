package io.ids.argus.job.base;

import io.grpc.netty.shaded.io.netty.channel.epoll.Epoll;
import io.grpc.netty.shaded.io.netty.channel.unix.DomainSocketAddress;
import io.ids.argus.grpc.base.AddressType;
import io.ids.argus.grpc.base.ArgusAddress;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class JobAddress extends ArgusAddress {
    public JobAddress() throws IOException {
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
        return Epoll.isAvailable() ? AddressType.DOMAIN : AddressType.INET;
    }

    @Override
    public int getPort() {
        return JobProperties.get().getPort();
    }

    @Override
    public String getFD() {
        return JobProperties.get().getFD();
    }
}
