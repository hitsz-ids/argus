package io.ids.argus.module.controller.outputs;

import io.ids.argus.core.base.enviroment.invoker.IInvokeOutput;

public class StopJobOutput implements IInvokeOutput {
    private int code;
    private String msg;

    public StopJobOutput(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
