package io.ids.argus.job.impl;

import io.ids.argus.job.AbstractJob;

public class Job implements AbstractJob<JobParams, JobResult> {
    @Override
    public void onCreate(JobParams params) {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onFailed() {

    }

    @Override
    public JobResult onComplete() {
        return null;
    }
}
