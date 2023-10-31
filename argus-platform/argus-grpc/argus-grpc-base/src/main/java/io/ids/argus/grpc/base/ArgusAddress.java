package io.ids.argus.grpc.base;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The communicate address of Argus
 * <p>
 * It may be internet address or domain socket address
 */
public abstract class ArgusAddress {
    private final SocketAddress address;

    private static final String TMP_PATH = "/tmp/";

    protected ArgusAddress() throws IOException {
        AddressType type = getType();

        switch (type) {
            case INET -> address = initInetAddress(getPort());
            case DOMAIN -> address = innerInitDomainAddress(Paths.get(TMP_PATH + getFD()));
            default -> throw new IOException("Network type error.");
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
