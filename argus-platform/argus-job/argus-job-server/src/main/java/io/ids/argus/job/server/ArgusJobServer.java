package io.ids.argus.job.server;

import io.grpc.ServerInterceptors;
import io.grpc.ServerServiceDefinition;
import io.ids.argus.grpc.base.ArgusAddress;
import io.ids.argus.grpc.base.ArgusGrpcServer;
import io.ids.argus.job.server.interceptor.OpenInterceptor;
import io.ids.argus.job.server.sevice.JobService;

import java.util.List;

public class ArgusJobServer extends ArgusGrpcServer {
    protected ArgusJobServer(ArgusAddress address) {
        super(address);
    }

    @Override
    protected void addServices(List<ServerServiceDefinition> list) {
        list.add(ServerInterceptors.intercept(new JobService(),
                new OpenInterceptor()));
    }
}
