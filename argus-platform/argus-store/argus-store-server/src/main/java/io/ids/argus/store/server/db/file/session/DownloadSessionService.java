package io.ids.argus.store.server.db.file.session;

import io.grpc.stub.StreamObserver;
import io.ids.argus.store.grpc.file.DownloadRequest;
import io.ids.argus.store.grpc.file.DownloadResponse;
import io.ids.argus.store.grpc.file.FileDownloadStoreServiceGrpc;
import io.ids.argus.store.server.db.file.observer.DownloadServerObserver;

/**
 * Session GRPC service
 */
public class DownloadSessionService extends FileDownloadStoreServiceGrpc.FileDownloadStoreServiceImplBase  {

    @Override
    public StreamObserver<DownloadRequest> download(StreamObserver<DownloadResponse> responseObserver) {
        return new DownloadServerObserver(responseObserver);
    }
}
