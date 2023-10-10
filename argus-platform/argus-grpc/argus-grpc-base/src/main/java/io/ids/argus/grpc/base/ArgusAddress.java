package io.ids.argus.grpc.base;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public abstract class ArgusAddress {
    private final SocketAddress address;

    protected ArgusAddress() throws IOException {
        AddressType type = getType();
        var sb = new StringBuilder("/tmp/");
        sb.append(getFD());
        if (Objects.equals(type, AddressType.INET)) {
            address = initInetAddress(getPort());
        } else if (Objects.equals(type, AddressType.DOMAIN)) {
            address = innerInitDomainAddress(Paths.get(sb.toString()));
        } else {
            throw new IOException("network type error");
        }
    }

    protected abstract SocketAddress initInetAddress(int port);

    protected abstract SocketAddress initDomainAddress(String path);

    private SocketAddress innerInitDomainAddress(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
        return initDomainAddress(path.toFile().getPath());
    }

    public abstract AddressType getType();

    public SocketAddress getAddress() {
        return address;
    }

    public abstract int getPort();

    public abstract String getFD();
}
