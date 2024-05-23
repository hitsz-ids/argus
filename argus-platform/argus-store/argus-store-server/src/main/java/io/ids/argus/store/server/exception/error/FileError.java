package io.ids.argus.store.server.exception.error;


import io.ids.argus.core.conf.exception.IError;

/**
 * Argus Store Session error code
 */
public enum FileError implements IError {

    NOT_FOUND_FILE (33001, "The file was not found."),
    FILE_SESSION_DOWNLOAD_ERROR (33002, "File download failed"),
    FILE_SESSION_UPLOAD_ERROR (33003, "File upload failed"),
    FILE_EXISTS_ERROR (33004, "File has exists"),

            ;
    private final int code;
    private final String msg;

    FileError(int code, String msg) {
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
