package io.ids.argus.store.base;

import io.ids.argus.core.conf.log.ArgusLogger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;


public class StorageUtils {
    private static final ArgusLogger log = new ArgusLogger(StorageUtils.class);
    private StorageUtils() {
        throw new UnsupportedOperationException("no support instance");
    }

    public static Throwable toThrowable(byte[] bytes, byte[] errorMsg) {
        Object obj;
        if (bytes == null) {
            obj = new StackTraceElement[0];
        } else {
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes)) {
                try (ObjectInputStream ois = new ObjectInputStream(bis)) {
                    obj = ois.readObject();
                }
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
                obj = new StackTraceElement[0];
            }
        }
        Throwable exception;
        if (errorMsg == null) {
            exception = new Throwable("NULL");
        } else {
            exception = new Throwable(new String(errorMsg, StandardCharsets.UTF_8));
        }
        exception.setStackTrace((StackTraceElement[]) obj);
        return exception;
    }

    public static byte[] toBytes(Throwable throwable) {
        byte[] bytes;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                oos.writeObject(throwable.getStackTrace());
                oos.flush();
                bytes = bos.toByteArray();
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            bytes = new byte[0];
        }
        return bytes;
    }
}