package io.ids.argus.job.client;

import io.ids.argus.core.conf.log.ArgusLogger;
import io.ids.argus.job.client.job.IJobParams;
import io.ids.argus.job.client.job.IJobResult;

import java.util.Objects;
import java.util.concurrent.Future;

public abstract class AArgusJob<P extends IJobParams, R extends IJobResult> {
    private final ArgusLogger log = new ArgusLogger(AArgusJob.class);
    private final String seq;
    private final P params;
    private Future<R> future;
    private R r;
    private JobState mState = JobState.WAITE;

    protected AArgusJob(String seq, String params) {
        super();
        this.seq = seq;
        this.params = transform(params);
    }

    R run() {
        mState = JobState.EXECUTE;
        return onRun();
    }

    void stop() {
        mState = JobState.STOPPED;
        onStop();
    }

    void complete() {
        mState = JobState.COMPLETED;
        onComplete();
    }

    void failed() {
        mState = JobState.FAILED;
        onFailed();
    }

    public abstract P transform(String params);

    public abstract void onFailed();

    public abstract R onRun();

    public abstract void onStop();

    public abstract void onComplete();

    public P getParams() {
        return params;
    }

    public String getSeq() {
        return seq;
    }

    public boolean running() {
        return !isDone() && !isCancelled();
    }

    public R getResult() {
        return r;
    }

    boolean isCancelled() {
        return future.isCancelled();
    }

    void setFuture(Future<R> future) {
        this.future = future;
    }

    void setResult(R r) {
        this.r = r;
    }

    boolean isDone() {
        return future.isDone();
    }

    void cancel() {
        future.cancel(false);
    }

    void stashState(JobState state) {
        if (!Objects.equals(mState, JobState.EXECUTE)) {
            return;
        }
        try {
            switch (state) {
                case STOPPED -> stop();
                case COMPLETED -> complete();
                case FAILED -> failed();
                default -> {}
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
