package io.ids.argus.store.server.db.file;

import io.ids.argus.store.server.db.file.entity.FileEntity;
import io.ids.argus.store.server.db.mapper.ArgusMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FileMapper extends ArgusMapper<FileEntity> {
    String selectFileIdByFileName(@Param("module") String module, @Param("moduleVersion") String moduleVersion,  @Param("directory") String directory, @Param("fileName") String fileName);
    List<FileEntity> selectFileByStatus(@Param("status") Integer status);

}
