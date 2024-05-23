package io.ids.argus.store.server.constant;

import lombok.*;

@Getter
@AllArgsConstructor
public enum FileStatus {
    UNKNOWN(0,"unknown"),
    UPLOADING(1,"uploading"),
    SUCCESS(2,"success"),
    FAIL(3,"fail");

    private final int code;
    private final String name;
}
