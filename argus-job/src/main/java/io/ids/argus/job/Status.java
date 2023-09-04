package io.ids.argus.job;

public enum Status {
    WAITING(1),
    RUNNING(2),
    STOPPED(3),
    FAILED(4);

    private final int code;

    Status(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
