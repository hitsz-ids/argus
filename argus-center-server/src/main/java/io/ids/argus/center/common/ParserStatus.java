package io.ids.argus.center.common;

import io.ids.argus.core.base.common.IStatus;
import io.ids.argus.store.client.session.JobSession;

public enum ParserStatus implements IStatus {
    NOT_FOUND_MODULE(12000, "未解析到对应【module】字段"),
    NOT_FOUND_VERSION(12001, "未解析到对应【version】字段"),
    NOT_FOUND_NAMESPACE(12002, "未解析到对应【namespace】字段"),
    NOT_FOUND_URL(12003, "未解析到对应【url】字段"),
    NULL_ADDRESS(12004, "address为空"),
    ;
    private final int code;
    private final String msg;

    ParserStatus(int code, String msg) {
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
