package io.ids.argus.store.server;

import io.grpc.ServerInterceptors;
import io.grpc.ServerServiceDefinition;
import io.ids.argus.grpc.base.ArgusAddress;
import io.ids.argus.grpc.base.ArgusGrpcServer;
import io.ids.argus.store.server.db.file.session.DownloadSessionService;
import io.ids.argus.store.server.db.file.session.UploadSessionService;
import io.ids.argus.store.server.db.job.session.JobStoreService;
import io.ids.argus.store.server.interceptor.RequestInterceptor;
import io.ids.argus.store.server.interceptor.SessionInterceptor;
import io.ids.argus.store.server.service.SessionService;

import java.util.List;

/**
 * Argus Store Server
 * <p>
 * Define the services and interceptors of argus GRPC store server
 */
public class ArgusStoreServer extends ArgusGrpcServer {
    protected ArgusStoreServer(ArgusAddress address) {
        super(address);
    }

    @Override
    protected void addServices(List<ServerServiceDefinition> list) {
        list.add(ServerInterceptors.intercept(
                new SessionService(),
                new SessionInterceptor()));
        list.add(ServerInterceptors.intercept(
                new JobStoreService(),
                new RequestInterceptor()));
        list.add(ServerInterceptors.intercept(
                new UploadSessionService(),
                new RequestInterceptor()));
        list.add(ServerInterceptors.intercept(
                new DownloadSessionService(),
                new RequestInterceptor()));
    }
}
