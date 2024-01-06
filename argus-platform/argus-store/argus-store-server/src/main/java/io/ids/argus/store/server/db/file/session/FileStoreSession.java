package io.ids.argus.store.server.db.file.session;

import io.ids.argus.store.server.db.file.FileMapper;
import io.ids.argus.store.server.db.file.entity.FileEntity;
import io.ids.argus.store.server.db.file.params.CreateFileParams;
import io.ids.argus.store.server.session.ArgusSqlStoreSession;

import java.util.Date;
import java.util.UUID;

/**
 * Job Store Session is used to operate the database data of Job
 */
public class FileStoreSession extends ArgusSqlStoreSession<FileMapper> {

    @Override
    public Class<FileMapper> getMapper() {
        return FileMapper.class;
    }

    public void createFile(CreateFileParams params) {
        var fileEntity = FileEntity.builder()
                .module(params.getModule())
                .moduleVersion(params.getModuleVersion())
                .filePath(params.getDirectory())
                .fileName(params.getFileName())
                .fileId(UUID.randomUUID().toString())
                .status(0)
                .createdTime(new Date())
                .build();
        mapper.insert(fileEntity);
    }
}
