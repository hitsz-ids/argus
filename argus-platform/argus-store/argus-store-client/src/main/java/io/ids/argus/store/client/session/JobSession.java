package io.ids.argus.store.client.session;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.ids.argus.store.grpc.SessionType;
import io.ids.argus.store.grpc.job.*;

public class JobSession extends StoreSession<JobStoreServiceGrpc.JobStoreServiceBlockingStub> {
    public JobSession(ManagedChannel channel) {
        super(channel);
    }

    @Override
    protected JobStoreServiceGrpc.JobStoreServiceBlockingStub getStub(Channel channel) {
        return JobStoreServiceGrpc.newBlockingStub(channel);
    }

    @Override
    protected SessionType getType() {
        return SessionType.JOB;
    }

    public CreateResponse create(CreateRequest request) {
        return stub.create(request);
    }

    public UpdateStatusResponse updateStatus(UpdateStatusRequest request) {
        return stub.updateStatus(request);
    }

    public JobStoreStatusEnum readState(String seq) {
        return stub.readStatus(ReadStateRequest.newBuilder()
                .setSeq(seq)
                .build()).getStatus();
    }

    public ListJobResponse listJob(ListJobRequest request) {
        return stub.listJob(request);
    }

}
