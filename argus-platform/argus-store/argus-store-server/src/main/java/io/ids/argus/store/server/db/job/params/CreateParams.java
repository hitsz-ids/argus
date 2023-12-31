package io.ids.argus.store.server.db.job.params;

import io.ids.argus.store.grpc.job.JobStoreStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Job Session Create Params
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateParams {

    private String module;
    private String moduleVersion;
    private String params;
    private String job;
    private String name;
    private JobStoreStatusEnum status;

}
