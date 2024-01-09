package io.ids.argus.store.server.task;

import io.ids.argus.core.conf.log.ArgusLogger;
import io.ids.argus.store.server.db.file.FileMapper;


public class FileTask {
    private FileMapper fileMapper;
    private static final ArgusLogger log = new ArgusLogger(FileTask.class);

    public void clearUploadFiles() {
        log.info("clear temp file");
//        List<FileEntity> files = fileMapper.selectFileByStatus(FileStatus.FAIL.getCode());
//        for (FileEntity file : files) {
//            File deleteFile = new File(file.getFilePath());
//            if(deleteFile.exists() && deleteFile.isFile()){
//                // delete file
//                if(deleteFile.delete()){
//                    log.info("delete file {} success", file.getFilePath());
//                } else {
//                    log.error("delete file {} fail", file.getFilePath());
//                }
//            } else {
//                log.error("not found file {}", file.getFilePath());
//            }
//            file.setIsDeleted(1);
//            fileMapper.updateById(file);
//        }
    }
}
