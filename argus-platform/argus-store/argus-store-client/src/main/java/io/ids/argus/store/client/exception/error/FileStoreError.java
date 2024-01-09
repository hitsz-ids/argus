package io.ids.argus.store.client.exception.error;


import io.ids.argus.core.conf.exception.IError;

/**
 * Argus Store Session error code
 */
public enum FileStoreError implements IError {

    NOT_FOUND_FILE (81000, "The file was not found."),
    FILE_SESSION_UPLOAD_ERROR(81001, "File upload failed"),
    FILE_SESSION_ALREADY_UPLOAD (81002, "already uploaded"),
    FILE_SESSION_CLOSED (81003, "already closed"),
    FILE_SESSION_UPLOAD_TIME_OUT (81004, "File upload timed out"),
    FILE_SESSION_UPLOAD_INTERRUPTED(81005,"File upload was interrupted"),
    FILE_SESSION_UPLOAD_NOT_READY(81006,"File not ready to upload yet"),
    FILE_SESSION_SAVE_TIME_OUT (81007, "File save timed out"),
    FILE_SESSION_SAVE_HAS_SUCCESS (81008, "File save has completed"),
    FILE_SESSION_FAIL_MESSAGE_SEND_FAIL (81009, "File fail message send fail"),
    FILE_SESSION_FAIL_MESSAGE_TIME_OUT (81010, "File fail message time out"),
    FILE_SESSION_DOWNLOAD_ERROR(81011, "File download failed"),
    FILE_SESSION_ALREADY_DOWNLOAD (81012, "already download"),
    FILE_SESSION_DOWNLOAD_TIME_OUT (81013, "File download timed out"),
    FILE_SESSION_DOWNLOAD_INTERRUPTED(81014,"File download was interrupted"),
    FILE_SESSION_DOWNLOAD_NOT_READY(81005,"File not ready to download yet"),
    FILE_SESSION_DOWNLOAD_IS_SUCCESS (81006, "File download has completed"),

            ;
    private final int code;
    private final String msg;

    FileStoreError(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String getMsg() {
        return msg;
    }

    @Override
    public int getCode() {
        return code;
    }
}
