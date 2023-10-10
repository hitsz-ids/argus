package io.ids.argus.store.server.db.job.params;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ListJobParams {
    int status;
    String module;
    String version;
}
