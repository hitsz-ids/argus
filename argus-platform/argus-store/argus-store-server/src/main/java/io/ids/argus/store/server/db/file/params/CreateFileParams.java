package io.ids.argus.store.server.db.file.params;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFileParams {
    private String module;
    private String moduleVersion;
    private String directory;
    private String fileId;
    private String fileName;
}
