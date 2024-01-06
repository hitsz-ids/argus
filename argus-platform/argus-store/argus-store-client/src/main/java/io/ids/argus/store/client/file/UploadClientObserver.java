package io.ids.argus.store.client.file;

import com.google.protobuf.ByteString;
import io.ids.argus.core.conf.log.ArgusLogger;
import io.ids.argus.store.client.exception.ArgusFileStoreException;
import io.ids.argus.store.client.exception.error.FileStoreError;
import io.ids.argus.store.client.observer.BaseClientObserver;
import io.ids.argus.store.grpc.file.UploadRequest;
import io.ids.argus.store.grpc.file.UploadResponse;

import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class UploadClientObserver extends BaseClientObserver<UploadRequest, UploadResponse> {
    private static final ArgusLogger log = new ArgusLogger(UploadClientObserver.class);
    private final CountDownLatch readyLatch = new CountDownLatch(1);
    private final CountDownLatch saveLatch = new CountDownLatch(1);
    private final CountDownLatch closeLatch = new CountDownLatch(1);

    private boolean ready = false;
    private boolean uploading = false;
    private boolean save = false;
    private boolean close = false;


    @Override
    public void onNext(UploadResponse uploadResponse) {
        try {
            switch (uploadResponse.getResultCase()) {
                case READY: {
                    ready = true;
                    readyLatch.countDown();
                }
                break;
                case UPLOADING: {
                    uploading = false;
                    lock();
                    try {
                        signal();
                    } finally {
                        unlock();
                    }
                }
                break;
                case SAVE: {
                    save = true;
                    saveLatch.countDown();
                }
                break;
                case CLOSE: {
                    close = true;
                    closeLatch.countDown();
                }
                break;
                default:
                    break;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            onError(e);
            throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_UPLOAD_ERROR);
        }
    }

    public void ready(String fileName, long size, String moduleName,String directory) {
        // todo 是否所有地方都应该做三个校验？
        if (uploading) {
            throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_ALREADY_UPLOAD);
        }
        if(save){
            throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_SAVE_HAS_SUCCESS);
        }
        if (close) {
            throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_CLOSED);
        }
        try {
            boolean pass;
            UploadRequest.Ready.Builder ready = UploadRequest.Ready.newBuilder()
                    .setModuleName(moduleName)
                    .setFileName(fileName)
                    .setSize(size);
            if(Objects.nonNull(directory)){
                ready.setDirectoryName(directory);
            }
            UploadRequest uploadRequest = UploadRequest.newBuilder()
                    .setReady(ready.build())
                    .build();
            sender.onNext(uploadRequest);
            pass = isReadying();
            if (!pass) {
                throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_UPLOAD_TIME_OUT);
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_UPLOAD_INTERRUPTED);
        }
    }

    public void upload(InputStream stream) {
        lock();
        if (!ready) {
            throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_UPLOAD_NOT_READY);
        }
        if(save){
            throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_SAVE_HAS_SUCCESS);
        }
        if (close) {
            throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_CLOSED);
        }
        try {
            int len;
            byte[] bytes = new byte[3 * 1024 * 1024];
            boolean pass;
            while ((len = stream.read(bytes)) != -1) {
                uploading = true;
                sender.onNext(UploadRequest.newBuilder()
                        .setUpload(UploadRequest.Upload.newBuilder()
                                .setBytes(ByteString.copyFrom(bytes, 0, len))
                                .setLen(len))
                        .build());
                while (uploading){
                    pass = await();
                    if (!pass) {
                        throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_UPLOAD_TIME_OUT);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_UPLOAD_INTERRUPTED);
        } finally {
            unlock();
        }
    }

    public void save(){
        if (uploading) {
            throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_ALREADY_UPLOAD);
        }
        if(save){
            throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_SAVE_HAS_SUCCESS);
        }
        if (close) {
            throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_CLOSED);
        }
        try{
            sender.onNext(UploadRequest.newBuilder()
                    .setSave(UploadRequest.Save.newBuilder()
                            .build())
                    .build());
            boolean pass = isSaving();
            if(!pass){
                throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_SAVE_TIME_OUT);
            }
        } catch (Exception e){
            log.error(e.getMessage(), e);
            throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_UPLOAD_INTERRUPTED);
        }
    }

    public void close(){
        if (close) {
            throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_CLOSED);
        }
        sender.onNext(
            UploadRequest.newBuilder()
                    .setClose(UploadRequest.Close.newBuilder()
                            .build())
                    .build());
        try{
            boolean pass = isClose();
            if(!pass){
                throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_FAIL_MESSAGE_TIME_OUT);
            }
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_FAIL_MESSAGE_SEND_FAIL);
        }
    }

    private boolean isReadying() throws InterruptedException {
        return readyLatch.await(60, TimeUnit.SECONDS);
    }
    private boolean isClose() throws InterruptedException {
        return closeLatch.await(60, TimeUnit.SECONDS);
    }
    private boolean isSaving() throws InterruptedException {
        return saveLatch.await(60, TimeUnit.SECONDS);
    }
}
