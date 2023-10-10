package io.ids.argus.job.server.sevice;


import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.ids.argus.core.conf.log.ArgusLogger;
import io.ids.argus.job.base.JobGrpcContext;
import io.ids.argus.job.grpc.*;
import io.ids.argus.job.server.exception.ArgusJobServerCallbackException;
import io.ids.argus.job.server.manager.*;
import io.ids.argus.store.client.ArgusStore;
import io.ids.argus.store.client.session.JobSession;
import io.ids.argus.store.grpc.job.*;

public class JobService extends JobServiceGrpc.JobServiceImplBase {
    private final ArgusLogger log = new ArgusLogger(JobService.class);

    @Override
    public StreamObserver<JobOpenRequest> open(StreamObserver<JobOpenResponse> responseObserver) {
        return ConnectorManager.get().createConnector(JobGrpcContext.getConnectorId(), responseObserver);
    }

    @Override
    public void commit(JobCommitRequest request, StreamObserver<JobCommitResponse> responseObserver) {
        var connectorId = JobGrpcContext.getConnectorId();
        try (var session = ArgusStore.get().open(JobSession.class)) {
            var response = session.create(CreateRequest.newBuilder()
                    .setModule(connectorId.module())
                    .setModuleVersion(connectorId.version())
                    .setName(request.getName())
                    .setJob(request.getJob())
                    .setParams(request.getParams())
                    .build());
            var success = JobManager.get().execute(
                    new Job.JobData(response.getSeq(),
                            JobGrpcContext.getConnectorId(),
                            request.getParams(),
                            request.getName(),
                            request.getJob()));
            if (!success) {
                responseObserver.onNext(JobCommitResponse.newBuilder()
                        .setSeq(response.getSeq())
                        .setCode(Code.ERROR)
                        .build());
                responseObserver.onCompleted();
                return;
            }
            session.commit();
            responseObserver.onNext(JobCommitResponse.newBuilder()
                    .setSeq(response.getSeq())
                    .setCode(Code.SUCCESS)
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            responseObserver.onError(Status.INTERNAL.withDescription("任务调度服务异常").asException());
        }
    }

    private boolean remove(String seq, JobStoreStatusEnum status) {
        var success = JobManager.get().remove(seq);
        if (success) {
            updateStatus(seq, status);
        }
        return success;
    }

    @Override
    public void stop(JobStopRequest request, StreamObserver<JobStopResponse> responseObserver) {
        try {
            var success = remove(request.getSeq(), JobStoreStatusEnum.STOPPED);
            if (success) {
                responseObserver.onNext(JobStopResponse.newBuilder()
                        .setCode(Code.SUCCESS)
                        .setMsg("任务已停止")
                        .build());
            } else {
                success = JobManager.get().publishCommand(request.getSeq(),
                        Command.builder().operation(Operation.STOP).build());
                if (!success) {
                    responseObserver.onNext(JobStopResponse.newBuilder()
                            .setCode(Code.ERROR)
                            .setMsg("任务执行停止失败")
                            .build());
                } else {
                    responseObserver.onNext(JobStopResponse.newBuilder()
                            .setCode(Code.OPERATING)
                            .setMsg("任务正在执行停止操作")
                            .build());
                }
            }
        } catch (ArgusJobServerCallbackException e) {
            responseObserver.onNext(JobStopResponse.newBuilder()
                    .setCode(Code.NOT_FOUND)
                    .setMsg(e.getMsg())
                    .build());
        } finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public void complete(JobCompleteRequest request, StreamObserver<JobCompleteResponse> responseObserver) {
        try {
            boolean success = JobManager.get().publishCommand(request.getSeq(),
                    Command.builder().operation(Operation.COMPLETED).build());
            if (!success) {
                responseObserver.onNext(JobCompleteResponse.newBuilder()
                        .setCode(Code.ERROR)
                        .build());
            } else {
                responseObserver.onNext(JobCompleteResponse.newBuilder()
                        .setCode(Code.OPERATING)
                        .build());
            }
        } catch (ArgusJobServerCallbackException e) {
            responseObserver.onNext(JobCompleteResponse.newBuilder()
                    .setCode(Code.NOT_FOUND)
                    .build());
        } finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public void fail(JobFailRequest request, StreamObserver<JobFailResponse> responseObserver) {
        if (!JobManager.get().contains(request.getSeq())) {
            updateStatus(request.getSeq(), JobStoreStatusEnum.FAILED);
            responseObserver.onNext(JobFailResponse.newBuilder()
                    .setCode(Code.SUCCESS)
                    .build());
            responseObserver.onCompleted();
            return;
        }
        try {
            var success = remove(request.getSeq(), JobStoreStatusEnum.FAILED);
            if (success) {
                responseObserver.onNext(JobFailResponse.newBuilder()
                        .setCode(Code.SUCCESS)
                        .build());
                responseObserver.onCompleted();
            } else {
                success = JobManager.get().publishCommand(request.getSeq(),
                        Command.builder().operation(Operation.FAILED).build());
                if (!success) {
                    responseObserver.onNext(JobFailResponse.newBuilder()
                            .setCode(Code.ERROR)
                            .build());
                } else {
                    responseObserver.onNext(JobFailResponse.newBuilder()
                            .setCode(Code.OPERATING)
                            .build());
                    responseObserver.onCompleted();
                }
            }
        } finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public void listRecovery(ListRecoveryRequest request, StreamObserver<ListRecoveryResponse> responseObserver) {
        var connectorId = JobGrpcContext.getConnectorId();
        try (var session = ArgusStore.get().open(JobSession.class)) {
            var res = session.listJob(ListJobRequest.newBuilder()
                    .setModule(connectorId.module())
                    .setVersion(connectorId.version())
                    .setStatus(JobStoreStatusEnum.EXECUTING.getNumber())
                    .build());
            var listJob = res.getJobListList();
            var resBuilder = ListRecoveryResponse.newBuilder();
            for (JobStoreData job : listJob) {
                resBuilder.addJobList(JobData.newBuilder()
                        .setJob(job.getJob())
                        .setSeq(job.getSeq())
                        .setParams(job.getParams())
                        .build());
            }
            responseObserver.onNext(resBuilder.build());
            responseObserver.onCompleted();
        }
    }

    private void updateStatus(String seq, JobStoreStatusEnum status) {
        try (var session = ArgusStore.get().open(JobSession.class)) {
            session.updateStatus(UpdateStatusRequest.newBuilder()
                    .setStatus(status)
                    .setSeq(seq)
                    .build());
            session.commit();
        }
    }
}
