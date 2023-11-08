package io.ids.argus.extension.modelchecking.controller.condition.entity;

import io.ids.argus.extension.modelchecking.job.ModelScanJob;
import io.ids.argus.extension.modelchecking.job.ModelScanJobParams;
import io.ids.argus.extension.modelchecking.job.ModelScanJobResult;
import io.ids.argus.job.client.AArgusJob;
import io.ids.argus.job.client.job.JobEntity;

public class ModelScanJobEntity extends JobEntity<ModelScanJobParams, ModelScanJobResult> {
    public ModelScanJobEntity(ModelScanJobParams params) {
        super(params);
    }

    @Override
    public Class<? extends AArgusJob<ModelScanJobParams, ModelScanJobResult>> getJob() {
        return ModelScanJob.class;
    }

    @Override
    public String getName() {
        return "ModelScanJobEntity";
    }
}