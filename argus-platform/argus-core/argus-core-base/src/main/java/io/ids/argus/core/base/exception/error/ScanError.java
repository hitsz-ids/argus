package io.ids.argus.core.base.exception.error;

import io.ids.argus.core.conf.exception.IError;

/**
 * Argus core scan error code
 */
public enum ScanError implements IError {

    SCAN_NOT_FOUND_APPLICATION_ERROR    (91001, "Class ArgusApplication not found."),
    SCAN_NOT_FOUND_PKG_PATH_ERROR       (91002, "ArgusApplication package path not found."),
    SCAN_PKG_ERROR                      (91003, "Please set the correct package path."),
    SCAN_JAR_ERROR                      (91004, "Failed to scan specified jar file."),
    SCAN_NOT_FOUND_RES_ERROR            (91005, "Resource file not found."),
    GET_URL_ERROR                       (91006,"Failed to obtain url, please check the path."),
    GET_JAR_ERROR                       (91007,"Jar file does not exist, please check the path.")
    ;

    private final int code;
    private final String msg;

    ScanError(int code, String msg) {
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
