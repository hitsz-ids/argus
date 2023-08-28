package io.ids.argus.core.common;

public enum ScanStatus implements IStatus {
    SCAN_NOT_FOUND_APPLICATION_ERROR(91001, "未找到对应的ArgusApplication"),
    SCAN_NOT_FOUND_PKG_PATH_ERROR(91002, "未找到对应的ArgusApplication包路径"),
    SCAN_PKG_ERROR(91003, "请设置正确的包路径"),
    SCAN_JAR_ERROR(91004, "扫描jar文件获取失败"),
    SCAN_NOT_FOUND_RES_ERROR(91005, "未找到文件资源"),
    GET_URL_ERROR(91004,"url获取失败，请检查路径"),
    GET_JAR_ERROR(91005,"jar文件不存在，请检查路径")
    ;
    private final int code;
    private final String msg;

    ScanStatus(int code, String msg) {
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
