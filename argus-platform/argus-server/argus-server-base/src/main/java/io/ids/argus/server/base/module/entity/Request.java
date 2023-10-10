package io.ids.argus.server.base.module.entity;

import io.ids.argus.core.base.json.ArgusJson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Request {
    private final CountDownLatch latch = new CountDownLatch(1);
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final RequestBody body;
    private ArgusJson response;
    private Exception exception;

    public Request(RequestBody body) {
        this.body = body;
    }

    public boolean isException() {
        return !Objects.isNull(exception);
    }

    public boolean isSuccess() {
        return !isException();
    }

    public void exception(Throwable e) {
        exception = new Exception(e);
    }

    public Exception getException() {
        return exception;
    }

    public boolean await(long timeout) throws InterruptedException {
        return latch.await(timeout, TimeUnit.SECONDS);
    }

    public void write(byte[] bytes) throws IOException {
        outputStream.write(bytes);
    }

    public void countDown() {
        latch.countDown();
    }

    public String getUrl() {
        return body.getUrl();
    }

    public ArgusJson getCustomized() {
        return body.getCustomized();
    }

    public ArgusJson getParams() {
        return body.getParams();
    }

    public ByteArrayOutputStream getOutputStream() {
        return outputStream;
    }

    public ArgusJson getResponse() {
        return response;
    }

    public void setResponse(ArgusJson response) {
        this.response = response;
    }
}
