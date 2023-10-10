package io.ids.argus.core.base.exception.error;

import io.ids.argus.core.conf.exception.IError;

public enum InvokerError implements IError {
    NOT_FOUND_NAMESPACE(90000, "未解析到对应module"),
    NOT_FOUND_URL(90001, "未找到对应接口"),
    NOT_FOUND_INVOKER(90002, "未找到对应调用方法"),
    ERROR_INVOKE_RETURN(90003, "API接口返回值必须是接口InvokerOutput"),
    ERROR_INVOKER_PARAMS(90004, "请求参数必须是接口InvokerArgs"),
    ERROR_INVOKE(90005, "调用出错"),
    ERROR_PARSE_RETURN(90006, "返回值处理错误"),
    ERROR_INVOKE_JOB_RETURN(90007, "JOB接口返回值必须为JobEntity的子类"),

    ;
    private final int code;
    private final String msg;

    InvokerError(int code, String msg) {
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
