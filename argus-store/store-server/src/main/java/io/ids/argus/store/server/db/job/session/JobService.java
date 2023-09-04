package io.ids.argus.store.server.db.job.session;

import io.grpc.stub.StreamObserver;
import io.ids.argus.store.server.service.IService;
import io.ids.argus.store.transport.grpc.job.CreateRequest;
import io.ids.argus.store.transport.grpc.job.CreateResponse;
import io.ids.argus.store.transport.grpc.job.JobStoreServiceGrpc;

public class JobService extends JobStoreServiceGrpc.JobStoreServiceImplBase
        implements IService<JobSession> {
    @Override
    public void create(CreateRequest request, StreamObserver<CreateResponse> responseObserver) {
    }
}
