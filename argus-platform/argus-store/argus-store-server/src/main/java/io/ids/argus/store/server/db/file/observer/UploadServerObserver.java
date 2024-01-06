package io.ids.argus.store.server.db.file.observer;

import io.grpc.stub.StreamObserver;
import io.ids.argus.core.conf.log.ArgusLogger;
import io.ids.argus.store.grpc.file.UploadRequest;
import io.ids.argus.store.grpc.file.UploadResponse;
import io.ids.argus.store.server.exception.ArgusFileException;
import io.ids.argus.store.server.exception.error.FileError;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The observer of GRPC Session request
 */
public class UploadServerObserver implements StreamObserver<UploadRequest> {
    private static final ArgusLogger log = new ArgusLogger(UploadServerObserver.class);
    private final StreamObserver<UploadResponse> pusher;
    private final ReentrantLock lock = new ReentrantLock();
    private final String storageName = "storage";
    private boolean closed = false;
    private  FileOutputStream outputStream;
    private String fileName;
    private String moduleName;
    private String extensionName;
    private String directoryName;

    public UploadServerObserver(StreamObserver<UploadResponse> pusher) {
        this.pusher = pusher;
    }
    @Override
    public void onNext(UploadRequest request) {
        try {
            switch (request.getResultCase()) {
                case READY:
                    ready(request.getReady());
                    break;
                case UPLOAD:
                    upload(request.getUpload());
                    break;
                case SAVE:
                    save();
                    break;
                case CLOSE:
                    fail();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            onError(e);
            throw new ArgusFileException(FileError.FILE_SESSION_UPLOAD_ERROR);
        }
    }

    private void ready(UploadRequest.Ready ready) {
        // todo module name validate
        fileName = ready.getFileName();
        moduleName = ready.getModuleName();
        extensionName = ready.getExtensionName();
        directoryName = ready.getDirectoryName();
        this.readyUploadStream();
        // todo sql
        createFile();
        pusher.onNext(UploadResponse.newBuilder()
                .setReady(UploadResponse.Ready.newBuilder().build())
                .build());
    }
    public void createFile() {
//        var session = getSqlSession();
//        var params = CreateFileParams.builder()
//                .module(moduleName)
//                .moduleVersion("1.0.0")
//                .directory(directoryName)
//                .fileName(fileName)
//                .fileId(UUID.randomUUID().toString())
//                .build();
//        session.createFile(params);
    }
    private void upload(UploadRequest.Upload upload) throws IOException {
        log.info("uploading : {}", fileName);
        byte[] bytes = upload.getBytes().toByteArray();
        outputStream.write(bytes);
        pusher.onNext(UploadResponse.newBuilder()
                .setUploading(UploadResponse.Uploading
                        .newBuilder().build())
                .build());
    }

    private void save() throws IOException {
        log.info("saving : {}", fileName);
        outputStream.flush();
        outputStream.close();
        pusher.onNext(UploadResponse.newBuilder()
                .setSave(UploadResponse.Save.newBuilder().build()).build());
        this.close();
    }

    private void fail() throws IOException {
        log.info("upload file: {} fail", fileName);
        outputStream.close();
        // todo 删除临时文件
        pusher.onNext(UploadResponse.newBuilder()
                .setClose(UploadResponse.Close.newBuilder().build()).build());
        this.close();
    }


    private void readyUploadStream(){
        Path directoryPath = this.getUploadDirectoryPath();
        Path path = Paths.get(directoryPath + File.separator + fileName);
        log.info("ready upload : {}", path);
        try{
            boolean exists = Files.exists(directoryPath);
            if (!exists) {
                Files.createDirectories(directoryPath);
            } else {
                boolean isDirectory = Files.isDirectory(directoryPath);
                if (!isDirectory) {
                    Files.delete(directoryPath);
                    Files.createDirectories(directoryPath);
                }
            }
            Files.deleteIfExists(path);
            Files.createFile(path);
            outputStream = new FileOutputStream(path.toString());
        } catch (Exception e){
            log.error(e.getMessage(), e);
            throw new ArgusFileException(FileError.FILE_SESSION_UPLOAD_ERROR);
        }
    }

    private Path getUploadDirectoryPath(){
        StringBuilder sb = new StringBuilder();
        sb.append(storageName);
        sb.append(File.separator);
        if(StringUtils.isNoneBlank(moduleName)){
            sb.append(moduleName);
            sb.append(File.separator);
        }
        if(StringUtils.isNoneBlank(extensionName)){
            sb.append(extensionName);
            sb.append(File.separator);
        }
        if(StringUtils.isNoneBlank(directoryName)){
            sb.append(directoryName);
            sb.append(File.separator);
        }
        return Paths.get(sb.toString());
    }

    @Override
    public void onError(Throwable throwable) {
        try {
            fail();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onCompleted() {
        close();
    }

    public void close() {
        synchronized (lock) {
            if (closed) {
                return;
            }
            closed = true;
        }
    }
}
