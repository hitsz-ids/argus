package io.ids.argus.store.server.db.file.session;

import io.ids.argus.store.server.db.file.FileMapper;
import io.ids.argus.store.server.db.file.entity.FileEntity;
import io.ids.argus.store.server.db.file.params.CreateFileParams;
import io.ids.argus.store.server.db.file.result.SelectFileResult;
import io.ids.argus.store.server.session.ArgusSqlStoreSession;

import java.util.Date;
import java.util.UUID;

/**
 * Job Store Session is used to operate file
 */
public class FileStoreSession extends ArgusSqlStoreSession<FileMapper> {
    @Override
    public Class<FileMapper> getMapper() {
        return FileMapper.class;
    }

    public String createFile(CreateFileParams params) {
        var fileEntity = FileEntity.builder()
                .module(params.getModule())
                .moduleVersion(params.getModuleVersion())
                .filePath(params.getDirectory())
                .fileName(params.getFileName())
                .fileId(UUID.randomUUID().toString())
                .status(0)
                .isDeleted(0)
                .createdTime(new Date())
                .build();
        mapper.insert(fileEntity);
        return fileEntity.getFileId();
    }
    public void updateStatus(String fileId, Integer status) {
        mapper.updateStatus(fileId, status);
    }
    public SelectFileResult selectFileByFileId(String fileId) {
        return mapper.selectFileByFileId(fileId);
    }

}
