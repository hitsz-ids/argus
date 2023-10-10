package io.ids.argus.job.client.exception;

import io.ids.argus.core.conf.exception.ArgusException;
import io.ids.argus.job.client.error.JobError;

public class ArgusJobException extends ArgusException {
    public ArgusJobException(JobError status) {
        super(status);
    }
}
