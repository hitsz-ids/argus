package io.ids.argus.store.server.db.file;

import io.ids.argus.store.server.db.file.entity.FileEntity;
import io.ids.argus.store.server.db.file.result.SelectFileResult;
import io.ids.argus.store.server.db.mapper.ArgusMapper;
import org.apache.ibatis.annotations.Param;

public interface FileMapper extends ArgusMapper<FileEntity> {
    SelectFileResult selectFileByFileId(@Param("fileId") String fileId);
    /**
     * Update file status
     *
     * @param fileId file id
     * @param status file status
     */
    void updateStatus(@Param("fileId") String fileId, @Param("status") Integer status);
}
