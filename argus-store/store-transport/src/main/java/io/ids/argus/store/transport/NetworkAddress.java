package io.ids.argus.store.transport;

import io.grpc.netty.shaded.io.netty.channel.unix.DomainSocketAddress;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public abstract class NetworkAddress {
    private final SocketAddress address;
    private final NetworkType type;
    private static final int PORT = 5555;
    private static final String FD_PATH = "/tmp/argus_store.fd";

    protected NetworkAddress() throws IOException {
        type = getType();
        if (Objects.equals(type, NetworkType.INET)) {
            address = initInetAddress(PORT);
        } else if (Objects.equals(type, NetworkType.DOMAIN)) {
            address = initDomainAddress();
        } else {
            throw new IOException("network type error");
        }
    }

    private InetSocketAddress initInetAddress(int port) {
        return new InetSocketAddress(port);
    }

    private DomainSocketAddress initDomainAddress() throws IOException {
        var path = Paths.get(FD_PATH);
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
        return new DomainSocketAddress(path.toFile());
    }

    public abstract NetworkType getType();

    public SocketAddress getAddress() {
        return address;
    }
}
