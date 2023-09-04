package io.ids.argus.core.base.common;

public enum InvokerStatus implements IStatus {
    NOT_FOUND_NAMESPACE(90000, "未解析到对应module"),
    NOT_FOUND_URL(90001, "未找到对应接口"),
    NOT_FOUND_INVOKER(90002, "未找到对应调用方法"),
    ERROR_INVOKE_RETURN(90003, "方法返回值必须为InvokerResult的子类"),
    ERROR_INVOKER_PARAMS(90004, "请求参数必须为InvokerParams的子类"),
    ERROR_INVOKE(90005, "调用出错"),
    ERROR_PARSE_INVOKE_DATA(90006, "proto解析错误");
    private final int code;
    private final String msg;

    InvokerStatus(int code, String msg) {
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
