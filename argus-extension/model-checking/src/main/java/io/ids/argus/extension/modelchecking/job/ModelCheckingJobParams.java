package io.ids.argus.extension.modelchecking.job;

import io.ids.argus.job.client.job.IJobParams;

public class ModelCheckingJobParams implements IJobParams {
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
