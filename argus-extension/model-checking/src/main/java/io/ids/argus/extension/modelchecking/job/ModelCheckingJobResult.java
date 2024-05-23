package io.ids.argus.extension.modelchecking.job;

import io.ids.argus.job.client.job.IJobResult;

public class ModelCheckingJobResult implements IJobResult {
    private String result;
    public ModelCheckingJobResult(String result){
        this.result = result;
    }
}
