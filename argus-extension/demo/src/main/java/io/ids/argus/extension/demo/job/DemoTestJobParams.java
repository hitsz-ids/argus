package io.ids.argus.extension.demo.job;

import io.ids.argus.job.client.job.IJobParams;

public class DemoTestJobParams implements IJobParams {
    private String jobName;

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
}
