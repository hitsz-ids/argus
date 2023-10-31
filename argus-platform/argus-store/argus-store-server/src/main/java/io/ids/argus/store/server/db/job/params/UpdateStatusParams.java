package io.ids.argus.store.server.db.job.params;

import io.ids.argus.store.grpc.job.JobStoreStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusParams {

    private String seq;
    private JobStoreStatusEnum status;

}
