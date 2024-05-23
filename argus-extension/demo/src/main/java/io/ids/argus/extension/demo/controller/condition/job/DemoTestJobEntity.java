package io.ids.argus.extension.demo.controller.condition.job;

import io.ids.argus.extension.demo.job.DemoTestJob;
import io.ids.argus.extension.demo.job.DemoTestJobParams;
import io.ids.argus.extension.demo.job.DemoTestJobResult;
import io.ids.argus.job.client.AArgusJob;
import io.ids.argus.job.client.job.JobEntity;

public class DemoTestJobEntity extends JobEntity<DemoTestJobParams, DemoTestJobResult> {
    public DemoTestJobEntity(DemoTestJobParams params) {
        super(params);
    }

    @Override
    public Class<? extends AArgusJob<DemoTestJobParams, DemoTestJobResult>> getJob() {
        return DemoTestJob.class;
    }

    @Override
    public String getName() {
        return "DemoTestJobEntity";
    }
}
