package io.ids.argus.store.client.file;

import com.google.protobuf.ByteString;
import io.ids.argus.core.conf.log.ArgusLogger;
import io.ids.argus.store.client.exception.ArgusFileStoreException;
import io.ids.argus.store.client.exception.error.FileStoreError;
import io.ids.argus.store.client.observer.BaseClientObserver;
import io.ids.argus.store.grpc.file.DownloadRequest;
import io.ids.argus.store.grpc.file.DownloadResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class DownloadClientObserver extends BaseClientObserver<DownloadRequest, DownloadResponse> {
    private static final ArgusLogger log = new ArgusLogger(DownloadClientObserver.class);
    private final CountDownLatch readyLatch = new CountDownLatch(1);
    private final CountDownLatch successLatch = new CountDownLatch(1);
    private final CountDownLatch failLatch = new CountDownLatch(1);
    private boolean ready = false;
    private boolean downloading = false;
    private boolean success = false;
    private boolean fail = false;
    private byte[] bytes;

    @Override
    public void onNext(DownloadResponse downloadResponse) {
        try {
            switch (downloadResponse.getResultCase()) {
                case READY: {
                    ready = true;
                    readyLatch.countDown();
                }
                break;
                case DOWNLOAD: {
                    lock();
                    try {
                        ByteString byteString = downloadResponse.getDownload().getByte();
                        int len = downloadResponse.getDownload().getLen();
                        if (len > 0) {
                            bytes = byteString.toByteArray();
                        } else {
                            bytes = null;
                        }
                        downloading = false;
                        signal();
                    } finally {
                        unlock();
                    }
                }
                break;
                case SUCCESS: {
                    success = true;
                    successLatch.countDown();
                }
                break;
                case FAIL: {
                    fail = true;
                    failLatch.countDown();
                }
                break;
                default:
                    break;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_DOWNLOAD_ERROR);
        }
    }

    public void ready(String fileName, String module,String directory) throws IOException {
        if (downloading) {
            throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_ALREADY_DOWNLOAD);
        }
        if (success) {
            throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_CLOSED);
        }
        if (fail) {
            throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_CLOSED);
        }
        sender.onNext(
                DownloadRequest.newBuilder()
                    .setReady(DownloadRequest.Ready.newBuilder()
                        .setFileName(fileName)
                        .setModuleName(module)
                        .setDirectoryName(directory)
                        .build())
               .build()
        );
        try {
            boolean pass = isReadying();
            if (!pass) {
                throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_DOWNLOAD_TIME_OUT);
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_DOWNLOAD_INTERRUPTED);
        }
    }

    public void download(HttpServletResponse servletResponse) {
        lock();
        if (!ready) {
            throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_DOWNLOAD_NOT_READY);
        }
        if (success) {
            throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_CLOSED);
        }
        if (fail) {
            throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_CLOSED);
        }
        byte[] buffer = new byte[3 * 1024 * 1024];
        try (BufferedOutputStream outputStream = new BufferedOutputStream(servletResponse.getOutputStream())) {
            int len;
            while ((len = sliceDownload(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_DOWNLOAD_ERROR);
        } finally {
            unlock();
        }
    }

    private int sliceDownload(byte[] readBytes) {
        if (success) {
             return -1;
        }
        if (fail) {
            throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_CLOSED);
        }
        downloading = true;
        try {
            sender.onNext(
                DownloadRequest.newBuilder()
                .setDownload(DownloadRequest.Download.newBuilder()
                        .setLen(readBytes.length)
                        .build())
                .build()
            );
            while (downloading) {
                boolean pass = await();
                if (!pass) {
                    throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_DOWNLOAD_TIME_OUT);
                }
            }
            if (bytes != null) {
                System.arraycopy(bytes, 0, readBytes, 0, bytes.length);
                return bytes.length;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_DOWNLOAD_ERROR);
        }
        return -1;
    }
    public void success()  {
        if (success) {
            throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_CLOSED);
        }
        if (fail) {
            throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_CLOSED);
        }
        fail = true;
        sender.onNext(DownloadRequest.newBuilder()
                .setSuccess(DownloadRequest.Success.newBuilder()
                        .build())
                .build());
        try {
            boolean pass = isSuccess();
            if (!pass) {
                throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_DOWNLOAD_TIME_OUT);
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_DOWNLOAD_ERROR);
        }
    }
    public void fail() {
        if (success) {
            throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_CLOSED);
        }
        if (fail) {
            throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_CLOSED);
        }
        fail = true;
        sender.onNext(DownloadRequest.newBuilder()
                .setFail(DownloadRequest.FAIL.newBuilder()
                        .build())
                .build());
        try {
            boolean pass = isFailing();
            if (!pass) {
                throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_FAIL_MESSAGE_TIME_OUT);
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new ArgusFileStoreException(FileStoreError.FILE_SESSION_DOWNLOAD_ERROR);
        }
    }
    private boolean isReadying() throws InterruptedException {
        return readyLatch.await(60, TimeUnit.SECONDS);
    }
    private boolean isSuccess() throws InterruptedException {
        return successLatch.await(60, TimeUnit.SECONDS);
    }
    private boolean isFailing() throws InterruptedException {
        return failLatch.await(60, TimeUnit.SECONDS);
    }
}
