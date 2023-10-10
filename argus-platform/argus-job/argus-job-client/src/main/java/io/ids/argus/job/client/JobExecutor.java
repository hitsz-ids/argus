package io.ids.argus.job.client;

import com.google.protobuf.Any;
import io.ids.argus.job.client.error.JobError;
import io.ids.argus.job.client.exception.ArgusJobException;
import io.ids.argus.job.client.job.IJobParams;
import io.ids.argus.job.client.job.IJobResult;
import io.ids.argus.job.grpc.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

public class JobExecutor extends Thread {
    private final Map<String, AArgusJob<?, ?>> jobs = new ConcurrentHashMap<>();
    private final ArgusJobClient client;
    private final BlockingQueue<Retry> retryQueue = new LinkedBlockingQueue<>();

    private enum RetryEnum {
        STOP,
        FAIL,
        COMPLETE
    }

    private record Retry(String seq, RetryEnum retryEnum) {
    }

    JobExecutor(ArgusJobClient client) {
        this.client = client;
        start();
    }

    public <P extends IJobParams, R extends IJobResult> void execute(AArgusJob<P, R> job) {
        if (jobs.containsKey(job.getSeq())) {
            return;
        }
        CompletableFuture<R> future = CompletableFuture.supplyAsync(job::run);
        job.setFuture(future);
        jobs.put(job.getSeq(), job);
        future.thenAcceptAsync((r) -> {
            try {
                job.setResult(r);
                internalComplete(job.getSeq());
            } catch (Exception e) {
                internalFail(job.getSeq());
            }
        });
        future.exceptionallyAsync(throwable -> {
            internalFail(job.getSeq());
            return null;
        });
    }

    @Override
    public void run() {
        while (true) {
            try {
                var retry = retryQueue.take();
                TimeUnit.SECONDS.sleep(5);
                if (retry.retryEnum == RetryEnum.STOP) {
                    internalStop(retry.seq);
                } else if (retry.retryEnum == RetryEnum.FAIL) {
                    internalFail(retry.seq);
                } else if (retry.retryEnum == RetryEnum.COMPLETE) {
                    internalComplete(retry.seq);
                }
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    boolean receive(String seq, Any any) throws Exception {
        if (any.is(ExecuteState.class)) {
            var data = any.unpack(ExecuteState.class);
            var job = createJob(seq, data.getJob(), data.getParams());
            execute(job);
            return true;
        }
        var job = jobs.get(seq);
        if (Objects.isNull(job)) {
            return true;
        }
        if (any.is(FailedState.class)) {
            job.stashState(JobState.FAILED);
        } else if (any.is(CompleteState.class)) {
            job.stashState(JobState.COMPLETED);
        } else if (any.is(StopState.class)) {
            job.stashState(JobState.STOPPED);
        }
        jobs.remove(seq);
        return true;
    }

    public JobCommitResponse commit(JobCommitRequest request) {
        return getStub().commit(request);
    }

    public void internalComplete(String seq) {
        var res = getStub().complete(JobCompleteRequest.newBuilder()
                .setSeq(seq)
                .build());
        if (Objects.equals(res.getCode(), Code.ERROR)) {
            retryQueue.add(new Retry(seq, RetryEnum.COMPLETE));
        } else if (Objects.equals(res.getCode(), Code.NOT_FOUND)) {
            var job = jobs.remove(seq);
            if (!Objects.isNull(job)) {
                job.stashState(JobState.COMPLETED);
            }
        }
    }

    private void internalFail(String seq) {
        var code = getStub().fail(JobFailRequest.newBuilder()
                .setSeq(seq).build()).getCode();
        if (Objects.equals(code, Code.ERROR)) {
            retryQueue.add(new Retry(seq, RetryEnum.FAIL));
        } else {
            var job = jobs.remove(seq);
            if (!Objects.isNull(job)) {
                job.stashState(JobState.FAILED);
            }
        }
    }

    private void internalStop(String seq) {
        var code = getStub().stop(JobStopRequest.newBuilder().setSeq(seq).build()).getCode();
        if (Objects.equals(code, Code.ERROR)) {
            retryQueue.add(new Retry(seq, RetryEnum.STOP));
        } else {
            var job = jobs.remove(seq);
            if (!Objects.isNull(job)) {
                job.stashState(JobState.STOPPED);
                job.cancel();
            }
        }
    }

    public JobStopResponse stop(String seq) {
        var res = getStub().stop(JobStopRequest.newBuilder().setSeq(seq).build());
        var code = res.getCode();
        if (Objects.equals(code, Code.SUCCESS) ||
                Objects.equals(code, Code.OPERATING)) {
            var job = jobs.remove(seq);
            if (!Objects.isNull(job)) {
                job.stashState(JobState.STOPPED);
                job.cancel();
            }
        }
        return res;
    }

    public void recovery() {
        var res = getStub().listRecovery(ListRecoveryRequest.newBuilder().build());
        for (JobData jobData : res.getJobListList()) {
            var job = createJob(jobData.getSeq(), jobData.getJob(), jobData.getParams());
            var failResponse = getStub().fail(JobFailRequest.newBuilder()
                    .setSeq(jobData.getSeq()).build());
            if (failResponse.getCode() == Code.SUCCESS) {
                job.onFailed();
            } else {
                jobs.put(job.getSeq(), job);
            }
        }
    }

    private JobServiceGrpc.JobServiceBlockingStub getStub() {
        return client.getBlockingStub(
                JobServiceGrpc.class,
                JobServiceGrpc.JobServiceBlockingStub.class
        );
    }

    private AArgusJob<?, ?> createJob(String seq, String job, String params) {
        try {
            return (AArgusJob<?, ?>) Class.forName(job).getConstructor(String.class, String.class)
                    .newInstance(seq, params);
        } catch (ClassNotFoundException |
                 NoSuchMethodException |
                 InvocationTargetException |
                 InstantiationException |
                 IllegalAccessException e) {
            throw new ArgusJobException(JobError.ERROR_INIT);
        }
    }
}
