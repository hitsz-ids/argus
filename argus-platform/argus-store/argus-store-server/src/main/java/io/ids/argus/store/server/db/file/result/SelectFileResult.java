package io.ids.argus.store.server.db.file.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SelectFileResult {
    private Long id;
    private String  module;
    private String  moduleVersion;
    private String fileId;
    private String  fileName;
    private Integer status;
    private String filePath;
    private Integer isDeleted;
    private Date createdTime;
}
