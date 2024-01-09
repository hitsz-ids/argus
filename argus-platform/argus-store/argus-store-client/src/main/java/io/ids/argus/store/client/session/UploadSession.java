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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

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

    public void upload(String fileName, MultipartFile file, String module, String directory) throws IOException {
        uploadBytes(fileName,file.getBytes(),module,directory);
    }

    public void uploadBytes(String fileName, byte[] bytes, String module, String directory) {
        UploadClientObserver observer = new UploadClientObserver();
        StreamObserver<UploadRequest> sender = stub.upload(observer);
        observer.setSender(sender);
        InputStream stream = new ByteArrayInputStream(bytes);
        try {
            observer.ready(fileName, bytes.length, module, directory);
            observer.upload(stream);
            observer.save();
//            readyCreateFile(fileName, module, directory);
            observer.onCompleted();
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            observer.close();
            observer.onError(e);
            throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_UPLOAD_ERROR);
        }
    }
//    public void createFile(CreateFileRequest request) {
//        var session = ArgusStore.get().open(CreateFileSession.class);
//        session.createFile(request);
//    }
//    private void readyCreateFile(String fileName, String module,  String directory){
//        CreateFileRequest request = CreateFileRequest.newBuilder()
//                .setModuleName(module)
//                .setDirectoryName(directory)
//                .setFileName(fileName)
//                .build();
//        createFile(request);
//    }
}
