package io.ids.argus.store.client.session;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import io.ids.argus.core.conf.log.ArgusLogger;
import io.ids.argus.store.client.exception.ArgusFileStoreException;
import io.ids.argus.store.client.exception.error.FileStoreError;
import io.ids.argus.store.client.file.DownloadClientObserver;
import io.ids.argus.store.grpc.SessionType;
import io.ids.argus.store.grpc.file.DownloadRequest;
import io.ids.argus.store.grpc.file.FileDownloadStoreServiceGrpc;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DownloadSession extends StoreSession<FileDownloadStoreServiceGrpc.FileDownloadStoreServiceStub> {
    private static final ArgusLogger log = new ArgusLogger(DownloadSession.class);

    public DownloadSession(ManagedChannel channel) {
        super(channel);
    }

    @Override
    protected FileDownloadStoreServiceGrpc.FileDownloadStoreServiceStub  getStub(Channel channel) {
        return FileDownloadStoreServiceGrpc.newStub(channel);
    }

    @Override
    protected SessionType getType() {
        return SessionType.FILE;
    }
    public void download(HttpServletResponse servletResponse, String fileName, String module,  String directory) throws IOException {
        DownloadClientObserver observer = new DownloadClientObserver();
        StreamObserver<DownloadRequest> sender = stub.download(observer);
        observer.setSender(sender);

        try {
            observer.ready(fileName, module, directory);
            observer.download(servletResponse);
            observer.success();
        } catch (Exception e) {
            observer.fail();
            log.error(e.getMessage(),e);
            throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_DOWNLOAD_ERROR);
        }
    }
}
