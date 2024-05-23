package io.ids.argus.store.server.db.file.observer;

import io.grpc.stub.StreamObserver;
import io.ids.argus.core.conf.log.ArgusLogger;
import io.ids.argus.store.grpc.SessionType;
import io.ids.argus.store.grpc.file.UploadRequest;
import io.ids.argus.store.grpc.file.UploadResponse;
import io.ids.argus.store.server.constant.FileStatus;
import io.ids.argus.store.server.db.file.params.CreateFileParams;
import io.ids.argus.store.server.db.file.session.FileStoreSession;
import io.ids.argus.store.server.exception.ArgusFileException;
import io.ids.argus.store.server.exception.error.FileError;
import io.ids.argus.store.server.service.BaseServerObserver;
import io.ids.argus.store.server.service.IService;
import io.ids.argus.store.server.session.ArgusStoreSession;
import io.ids.argus.store.server.session.SessionFactory;
import io.ids.argus.store.server.session.SessionManager;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The observer of GRPC Session request
 */
public class UploadServerObserver extends BaseServerObserver<UploadResponse, UploadRequest> implements IService<FileStoreSession> {
    private static final ArgusLogger log = new ArgusLogger(UploadServerObserver.class);
    private final StreamObserver<UploadResponse> pusher;
    private final ReentrantLock lock = new ReentrantLock();
    private boolean closed = false;
    private final String sessionId;
    private final ArgusStoreSession session;
    private FileOutputStream outputStream;
    private String fileName;
    private String moduleName;
    private String directoryName;
    private String filePath;
    private String fileId;

    public UploadServerObserver(StreamObserver<UploadResponse> pusher) {
        super(pusher);
        this.sessionId = SessionManager.get().generateId();
        this.pusher = pusher;
        session = SessionFactory.create(SessionType.FILE);
        SessionManager.get().add(sessionId, session);
    }

    private void ready(UploadRequest.Ready ready) {
        fileName = ready.getFileName();
        moduleName = ready.getModuleName();
        directoryName = ready.getDirectoryName();
        this.readyUploadStream();
        createFile();
        session.commit();
        pusher.onNext(UploadResponse.newBuilder()
                .setReady(UploadResponse.Ready.newBuilder().build())
                .build());
    }

    public void createFile() {
        log.info("createFile: {}", fileName);
        var session = getSqlSession();
        var params = CreateFileParams.builder()
                .module(moduleName)
                // 现在都使用默认版本 1.0.0
                .moduleVersion("1.0.0")
                .directory(directoryName)
                .fileName(fileName)
                .fileId(UUID.randomUUID().toString())
                .build();
        fileId = session.createFile(params);
    }

    private void upload(UploadRequest.Upload upload) throws IOException {
        log.info("uploading : {}", fileName);
        byte[] bytes = upload.getBytes().toByteArray();
        var session = getSqlSession();
        session.updateStatus(fileId, FileStatus.UPLOADING.getCode());
        session.commit();
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
        var session = getSqlSession();
        session.updateStatus(fileId, FileStatus.SUCCESS.getCode());
        session.commit();
        pusher.onNext(UploadResponse.newBuilder()
                .setSave(UploadResponse.Save.newBuilder().build()).build());
        this.onCompleted();
    }

    private void fail() throws IOException {
        log.info("upload file: {} fail", fileName);
        outputStream.close();
        var session = getSqlSession();
        session.updateStatus(fileId, FileStatus.FAIL.getCode());
        // delete tempe file
        Path tempFilePath = Paths.get(this.filePath);
        try {
            boolean deleted = Files.deleteIfExists(tempFilePath);
            if (deleted) {
                log.info("Temporary file {} deleted.", fileName);
            } else {
                log.error("Temporary file {} does not exist.", fileName);
            }
        } catch (IOException e) {
            log.error("Error occurred while deleting temporary file: {}", e.getMessage());
        }
        pusher.onNext(UploadResponse.newBuilder()
                .setClose(UploadResponse.Close.newBuilder().build()).build());
        this.close();
    }

    private void readyUploadStream() {
        Path directoryPath = this.getUploadDirectoryPath();
        Path path = Paths.get(directoryPath + File.separator + fileName);
        log.info("ready upload : {}", path);
        try {
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
            if (Files.exists(path)) {
                throw new ArgusFileException(FileError.FILE_EXISTS_ERROR);
            } else {
                Files.createFile(path);
            }
            this.filePath = path.toString();
            outputStream = new FileOutputStream(path.toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            exception(e);
        }
    }

    private Path getUploadDirectoryPath() {
        StringBuilder sb = new StringBuilder();
        sb.append("storage");
        sb.append(File.separator);
        if (StringUtils.isNoneBlank(moduleName)) {
            sb.append(moduleName);
            sb.append(File.separator);
        }
        if (StringUtils.isNoneBlank(directoryName)) {
            sb.append(directoryName);
            sb.append(File.separator);
        }
        return Paths.get(sb.toString());
    }

    public void close() {
        synchronized (lock) {
            if (closed) {
                return;
            }
            closed = true;
        }
        var session = SessionManager.get().remove(sessionId);
        if (Objects.isNull(session)) {
            session = this.session;
        }
        session.close();
    }

    @Override
    public void onNext(UploadRequest request) {
        try {
            switch (request.getResultCase()) {
                case READY -> ready(request.getReady());
                case UPLOAD -> upload(request.getUpload());
                case SAVE -> save();
                case CLOSE -> fail();
                default -> {
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            exception(e);
            throw new ArgusFileException(FileError.FILE_SESSION_UPLOAD_ERROR);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        try {
            fail();
        } catch (IOException e) {
            throw new ArgusFileException(FileError.FILE_SESSION_UPLOAD_ERROR);
        }
    }

    @Override
    public void onCompleted() {
        close();
    }

}
