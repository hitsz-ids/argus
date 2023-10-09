package io.ids.argus.extension.demo;

import io.ids.argus.core.base.exception.ArgusScannerException;
import io.ids.argus.core.base.module.annotation.ArgusApplication;
import io.ids.argus.module.ArgusModule;

@ArgusApplication(pkg = "io.ids.argus.extension.demo")
public class DemoApplication {

    public static void main(String[] args) throws
            InterruptedException,
            ArgusScannerException {
        ArgusModule.start(DemoApplication.class);
        Thread.currentThread().join();
    }
}
