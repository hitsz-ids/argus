package io.ids.argus.job.grpc;

import io.grpc.Metadata;

public class GrpcConstants {
    public static final Metadata.Key<String> MODULE_HEADER =
            Metadata.Key.of("module", Metadata.ASCII_STRING_MARSHALLER);
    public static final Metadata.Key<String> VERSION_HEADER =
            Metadata.Key.of("version", Metadata.ASCII_STRING_MARSHALLER);


}
