package io.ids.argus.center.startup;

import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.netty.handler.ssl.ClientAuth;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslProvider;
import io.ids.argus.center.conf.CenterProperties;
import io.ids.argus.core.base.enviroment.context.ArgusScanner;
import io.ids.argus.core.conf.log.ArgusLogger;
import io.ids.argus.server.base.ArgusServer;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * The bootstrap class of Argus center server
 */
class ArgusCenterServer {
    public final ArgusLogger log = new ArgusLogger(ArgusCenterServer.class);

    private final ArgusServer argusServer;
    private final ArgusScanner scanner = new ArgusScanner();

    ArgusCenterServer() {
        argusServer = new ArgusServer(
                CenterProperties.get().getPort(),
                getSslContext(),
                CenterProperties.get().getModules(),
                CenterProperties.get().getModulesDir()
        );
    }

    public void start(Class<?> primarySource) throws IOException {
        scanner.scan(primarySource);
        argusServer.start();
        log.debug("Argus Center Server started.");
    }

    public void awaitTermination() throws InterruptedException {
        argusServer.awaitTermination();
    }

    private SslContext getSslContext() {
        try {
            var publicPath = Paths.get(CenterProperties.get().getPublicPath());
            var ca = Paths.get(CenterProperties.get().getCaPublic());
            var pkcs8 = Paths.get(CenterProperties.get().getPKcs8());
            var sslClientContextBuilder = SslContextBuilder.forServer(
                    publicPath.toFile(),
                    pkcs8.toFile());
            sslClientContextBuilder.trustManager(ca.toFile());
            sslClientContextBuilder.clientAuth(ClientAuth.REQUIRE);
            return GrpcSslContexts.configure(sslClientContextBuilder,
                    SslProvider.OPENSSL).build();
        } catch (SSLException e) {
            return null;
        }
    }

}
