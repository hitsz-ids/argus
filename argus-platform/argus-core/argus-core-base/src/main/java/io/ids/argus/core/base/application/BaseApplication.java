package io.ids.argus.core.base.application;

import io.ids.argus.core.base.callback.UnaryCallback;

import java.util.Objects;

/**
 * @author jalr4real[jalrhex@gmail.com]
 * @since 2023-11-02
 */
public abstract class BaseApplication {

    private static UnaryCallback runningListener = null;

    protected static void boot() {
       if (Objects.nonNull(runningListener)) {
           runningListener.call();
           runningListener = null;
       }
    }


    public static void register(UnaryCallback callback) {
        runningListener = callback;
    }

}
