package io.ids.argus.center.common;

import io.ids.argus.core.common.IStatus;

public enum NetworkStatus implements IStatus {
    UNAUTHENTICATED(11000, "模块未授权"),
    NOT_FOUND(11001, "未找到对应模块配置"),
    ALREADY_EXISTS(11002, "模块已经登录，请不要重复登录"),
    NOT_FOUND_COMMAND(11003, "未找到对应的请求id")
    ;
    private final int code;
    private final String msg;

    NetworkStatus(int code, String msg) {
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
