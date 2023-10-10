package io.ids.argus.store.server.db.job.session;

import io.grpc.stub.StreamObserver;
import io.ids.argus.store.grpc.job.*;
import io.ids.argus.store.server.db.job.params.CreateParams;
import io.ids.argus.store.server.db.job.params.ListJobParams;
import io.ids.argus.store.server.db.job.params.UpdateStatusParams;
import io.ids.argus.store.server.service.IService;

public class JobStoreService extends JobStoreServiceGrpc.JobStoreServiceImplBase
        implements IService<JobSqlStoreSession> {
    @Override
    public void create(CreateRequest request, StreamObserver<CreateResponse> responseObserver) {
        var session = getSqlSession();
        var result = session.create(CreateParams.builder()
                .module(request.getModule())
                .moduleVersion(request.getModuleVersion())
                .job(request.getJob())
                .params(request.getParams())
                .name(request.getName())
                .status(JobStoreStatusEnum.WAITING)
                .build());
        responseObserver.onNext(CreateResponse.newBuilder()
                .setId(result.getId())
                .setSeq(result.getSeq())
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void updateStatus(UpdateStatusRequest request, StreamObserver<UpdateStatusResponse> responseObserver) {
        var session = getSqlSession();
        session.updateStatus(UpdateStatusParams.builder()
                .seq(request.getSeq())
                .status(request.getStatus())
                .build());
        responseObserver.onNext(UpdateStatusResponse.newBuilder().build());
        responseObserver.onCompleted();

    }

    @Override
    public void listJob(ListJobRequest request, StreamObserver<ListJobResponse> responseObserver) {
        var session = getSqlSession();
        var result = session.listJob(ListJobParams.builder()
                .version(request.getVersion())
                .module(request.getModule())
                .status(request.getStatus())
                .build());
        responseObserver.onNext(ListJobResponse.newBuilder()
                .addAllJobList(result.getList())
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void readStatus(ReadStateRequest request, StreamObserver<ReadStateResponse> responseObserver) {
        var session = getSqlSession();
        var status = session.readState(request.getSeq());
        responseObserver.onNext(ReadStateResponse.newBuilder()
                        .setStatus(status)
                .build());
        responseObserver.onCompleted();
    }
}
