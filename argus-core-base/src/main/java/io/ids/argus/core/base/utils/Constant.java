package io.ids.argus.core.base.utils;

public class Constant {
    // scanner
    public static final String FILE_SEPARATOR = "/";
    public static final String URL_SEPARATOR = "/";
    public static final String CLASS_FILE_SEPARATOR = ".";
    public static final String JAR_FILE_SUFFIX = "jar";
    public static final String CLASS_FILE_SUFFIX = ".class";

    // grpc header
    public static final String HEADER_MODULE_NAME = "module";
    public static final String HEADER_MODULE_VERSION = "version";
    public static final String HEADER_REQUEST_ID = "requestId";
    public static final int SEND_STREAM_MAX_SIZE = 3 * 1024 * 1024;
}
