package io.ids.argus.job.client;

import io.ids.argus.job.base.ConnectorId;
import io.ids.argus.job.base.JobAddress;
import io.ids.argus.job.client.error.JobError;
import io.ids.argus.job.client.exception.ArgusJobException;
import io.ids.argus.job.grpc.Code;
import io.ids.argus.job.grpc.JobCommitRequest;
import io.ids.argus.job.grpc.JobCommitResponse;
import io.ids.argus.job.grpc.JobStopResponse;

import java.io.IOException;
import java.util.Objects;

public final class ArgusJob {
    private static ArgusJobClient client;
    private static final ArgusJob instance = new ArgusJob();

    private ArgusJob() {
    }

    public static ArgusJob get() {
        if (Objects.isNull(client)) {
            throw new ArgusJobException(JobError.ERROR_PRE_INIT);
        }
        return instance;
    }

    public static void init(ConnectorId connectorId) {
        try {
            client = new ArgusJobClient(new JobAddress(), connectorId);
        } catch (IOException e) {
            throw new ArgusJobException(JobError.ERROR_INIT);
        }
    }

    public JobCommitResponse commit(JobCommitRequest data) {
        return client.commit(data);
    }

    public JobStopResponse stop(String seq) {
        return client.stop(seq);
    }

}
