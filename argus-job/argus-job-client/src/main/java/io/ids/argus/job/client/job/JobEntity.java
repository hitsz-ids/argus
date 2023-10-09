package io.ids.argus.job.client.job;

import io.ids.argus.job.client.AArgusJob;

public abstract class JobEntity<P extends IJobParams, R extends IJobResult> {
    protected final P params;

    public JobEntity(P params) {
        this.params = params;
    }

    public P getParams() {
        return params;
    }

    public abstract String getName();

    public abstract Class<? extends AArgusJob<P, R>> getJob();
}
