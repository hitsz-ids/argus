package io.ids.argus.module.entity;

import io.ids.argus.core.base.json.Transformer;

import java.nio.charset.StandardCharsets;

public class StopJobResponse {
    private int code;
    private String msg;

    public StopJobResponse(int code, String msg) {
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

    public static byte[] transmission() {
        return Transformer.toJsonString(new StopJobResponse(0, "任务正在执行停止"))
                .getBytes(StandardCharsets.UTF_8);
    }


}
