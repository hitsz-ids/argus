package io.ids.argus.store.client.session;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import io.ids.argus.core.conf.log.ArgusLogger;
import io.ids.argus.store.client.exception.ArgusFileStoreException;
import io.ids.argus.store.client.exception.error.FileStoreError;
import io.ids.argus.store.client.file.UploadClientObserver;
import io.ids.argus.store.grpc.SessionType;
import io.ids.argus.store.grpc.file.FileUploadStoreServiceGrpc;
import io.ids.argus.store.grpc.file.UploadRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class UploadSession extends StoreSession<FileUploadStoreServiceGrpc.FileUploadStoreServiceStub> {
    private static final ArgusLogger log = new ArgusLogger(UploadSession.class);

    public UploadSession(ManagedChannel channel) {
        super(channel);
    }

    @Override
    protected FileUploadStoreServiceGrpc.FileUploadStoreServiceStub getStub(Channel channel) {
        return FileUploadStoreServiceGrpc.newStub(channel);
    }

    @Override
    protected SessionType getType() {
        return SessionType.FILE;
    }

    public void upload(String fileName, MultipartFile file, String module, String directory) {
        UploadClientObserver observer = new UploadClientObserver();
        StreamObserver<UploadRequest> sender = stub.upload(observer);
        observer.setSender(sender);

        try (InputStream stream = file.getInputStream()) {
            observer.ready(fileName, file.getSize(), module, directory);
            uploadBytes(observer, stream);
            observer.save();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            observer.close();
            observer.onError(e);
            throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_UPLOAD_ERROR);
        }
    }

    public void upload(String fileName, InputStream stream, String module, String directory, long size) {
        UploadClientObserver observer = new UploadClientObserver();
        StreamObserver<UploadRequest> sender = stub.upload(observer);
        observer.setSender(sender);

        try (stream) {
            observer.ready(fileName, size, module, directory);
            uploadBytes(observer, stream);
            observer.save();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            observer.close();
            observer.onError(e);
            throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_UPLOAD_ERROR);
        }
    }

    public void uploadBytes(UploadClientObserver observer, InputStream stream) throws IOException {
        var bytes = new byte[3 * 1024 * 1024];
        int bytesRead;
        while ((bytesRead = stream.read(bytes)) != -1) {
            byte[] data = Arrays.copyOf(bytes, bytesRead);
            observer.upload(data);
        }
    }
}
