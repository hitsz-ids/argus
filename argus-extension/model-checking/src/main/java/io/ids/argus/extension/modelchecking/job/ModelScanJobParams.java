package io.ids.argus.extension.modelchecking.job;

import io.ids.argus.job.client.job.IJobParams;

public class ModelScanJobParams implements IJobParams {
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
