package io.ids.argus.store.server.db.job.result;

import io.ids.argus.store.grpc.job.JobStoreData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListJobResult {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class JobData {
        private String seq;
        private String job;
        private String params;
    }

    List<JobStoreData> list;

}
