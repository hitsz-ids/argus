package io.ids.argus.extension.modelchecking.job;

import io.ids.argus.job.client.job.IJobResult;

public class ModelScanJobResult implements IJobResult {
    private String result;
    public ModelScanJobResult(String result){
        this.result = result;
    }
}
