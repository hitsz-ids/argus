package io.ids.argus.server.grpc;

import io.grpc.Metadata;

public class GrpcConstants {
    public static final Metadata.Key<String> HEADER_REQUEST_ID =
            Metadata.Key.of("requestId", Metadata.ASCII_STRING_MARSHALLER);

    public static final Metadata.Key<String> HEADER_MODULE_NAME_SECRET =
            Metadata.Key.of("module", Metadata.ASCII_STRING_MARSHALLER);

    public static final Metadata.Key<String> HEADER_MODULE_VERSION =
            Metadata.Key.of("version", Metadata.ASCII_STRING_MARSHALLER);

    public static final int SEND_STREAM_MAX_SIZE = 3 * 1024 * 1024;
}
