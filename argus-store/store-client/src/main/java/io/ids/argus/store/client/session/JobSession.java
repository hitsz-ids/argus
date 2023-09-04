package io.ids.argus.store.client.session;

import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.Channel;
import io.ids.argus.store.client.network.StoreSession;
import io.ids.argus.store.transport.grpc.SessionType;
import io.ids.argus.store.transport.grpc.job.JobStoreServiceGrpc;

public class JobSession extends StoreSession<JobStoreServiceGrpc.JobStoreServiceBlockingStub> {
    public JobSession() throws InvalidProtocolBufferException {
        super();
    }

    @Override
    protected JobStoreServiceGrpc.JobStoreServiceBlockingStub getStub(Channel channel) {
        return JobStoreServiceGrpc.newBlockingStub(channel);
    }

    @Override
    protected SessionType getType() {
        return SessionType.JOB;
    }
}
