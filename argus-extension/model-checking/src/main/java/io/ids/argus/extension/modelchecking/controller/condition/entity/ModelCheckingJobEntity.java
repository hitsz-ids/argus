package io.ids.argus.extension.modelchecking.controller.condition.entity;

import io.ids.argus.extension.modelchecking.job.ModelCheckingJob;
import io.ids.argus.extension.modelchecking.job.ModelCheckingJobParams;
import io.ids.argus.extension.modelchecking.job.ModelCheckingJobResult;
import io.ids.argus.job.client.AArgusJob;
import io.ids.argus.job.client.job.JobEntity;

public class ModelCheckingJobEntity extends JobEntity<ModelCheckingJobParams, ModelCheckingJobResult> {
    public ModelCheckingJobEntity(ModelCheckingJobParams params) {
        super(params);
    }

    @Override
    public Class<? extends AArgusJob<ModelCheckingJobParams, ModelCheckingJobResult>> getJob() {
        return ModelCheckingJob.class;
    }

    @Override
    public String getName() {
        return "ModelScanJobEntity";
    }
}