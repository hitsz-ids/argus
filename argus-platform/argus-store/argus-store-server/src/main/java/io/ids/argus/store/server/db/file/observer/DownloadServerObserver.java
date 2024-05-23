package io.ids.argus.store.server.db.file.observer;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import io.ids.argus.core.conf.log.ArgusLogger;
import io.ids.argus.store.grpc.SessionType;
import io.ids.argus.store.grpc.file.DownloadRequest;
import io.ids.argus.store.grpc.file.DownloadResponse;
import io.ids.argus.store.grpc.file.UploadRequest;
import io.ids.argus.store.server.db.file.entity.FileEntity;
import io.ids.argus.store.server.db.file.result.SelectFileResult;
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
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The observer of GRPC Session request
 */
public class DownloadServerObserver extends BaseServerObserver<DownloadResponse,DownloadRequest> implements IService<FileStoreSession> {
    private static final ArgusLogger log = new ArgusLogger(DownloadServerObserver.class);
    private final StreamObserver<DownloadResponse> pusher;
    private final String id;
    private final ArgusStoreSession session;
    private final ReentrantLock lock = new ReentrantLock();
    private final String storageName = "storage";
    private boolean closed = false;
    private String fileId;
    private String fileName;
    private FileChannel channel;

    public DownloadServerObserver(StreamObserver<DownloadResponse> pusher) {
        super(pusher);
        this.id = SessionManager.get().generateId();
        this.pusher = pusher;
        session = SessionFactory.create(SessionType.FILE);
        SessionManager.get().add(id, session);
    }

    private void ready(DownloadRequest.Ready ready) throws IOException {
        fileId = ready.getFileId();
        var session = getSqlSession();
        SelectFileResult file = session.selectFileByFileId(fileId);
        Path directoryPath = this.getDownloadDirectoryPath(file.getModule(), file.getFilePath());

        fileName = file.getFileName();
        Path path = Paths.get(directoryPath + File.separator + fileName);
        boolean exists = Files.exists(path);
        if (!exists) {
            session.close();
            log.error("file is not found");
            throw new ArgusFileException(FileError.NOT_FOUND_FILE);
        }
        // set file channel
        channel = FileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.READ);
        pusher.onNext(DownloadResponse.newBuilder()
                .setReady(
                        DownloadResponse.Ready
                                .newBuilder()
                                .build())
                .build());
    }

    private void download(DownloadRequest.Download download) throws IOException {
        int len = download.getLen();
        byte[] bytes = new byte[len];
        int result = read(bytes);
        if (result != -1) {
            pusher.onNext(DownloadResponse.newBuilder()
                    .setDownload(DownloadResponse.Download.
                            newBuilder()
                            .setByte(ByteString.copyFrom(bytes, 0, result))
                            .setLen(result)
                            .build())
                    .build());
        } else {
            pusher.onNext(DownloadResponse.newBuilder()
                    .setDownload(DownloadResponse.Download.
                            newBuilder()
                            .setLen(0)
                            .build())
                    .build());
        }
    }

    private int read(byte[] bytes) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
        int len = channel.read(byteBuffer);
        // enter read mode
        byteBuffer.flip();
        if (len > 0) {
            byteBuffer.get(bytes, 0, len);
        }
        return len;
    }

    private Path getDownloadDirectoryPath(String moduleName, String filePath) {
        StringBuilder sb = new StringBuilder();
        sb.append(storageName);
        sb.append(File.separator);
        if (StringUtils.isNoneBlank(moduleName)) {
            sb.append(moduleName);
            sb.append(File.separator);
        }
        if (StringUtils.isNoneBlank(filePath)) {
            sb.append(filePath);
            sb.append(File.separator);
        }
        return Paths.get(sb.toString());
    }

    private void success() {
        pusher.onNext(DownloadResponse.newBuilder()
                .setSuccess(DownloadResponse.Success.newBuilder()
                        .build())
                .build());
        this.close();
    }

    private void fail() {
        pusher.onNext(DownloadResponse.newBuilder()
                .setFail(DownloadResponse.Fail.newBuilder()
                        .build())
                .build());
        this.close();
    }

    @Override
    public void onNext(DownloadRequest request) {
        try {
            switch (request.getResultCase()) {
                case READY:
                    ready(request.getReady());
                    log.info("ready download : {}", fileName);
                    break;
                case DOWNLOAD:
                    log.info("downloading : {}", fileName);
                    download(request.getDownload());
                    break;
                case SUCCESS:
                    log.info("success download file: {}", fileName);
                    success();
                    break;
                case FAIL:
                    log.info("fail download file: {}", fileName);
                    fail();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            exception(e);
            throw new ArgusFileException(FileError.FILE_SESSION_DOWNLOAD_ERROR);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        close();
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
        var session = SessionManager.get().remove(id);
        if (Objects.isNull(session)) {
            session = this.session;
        }
        session.close();
    }
}
