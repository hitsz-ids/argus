package io.ids.argus.extension.modelchecking;

import io.ids.argus.core.base.exception.ArgusScannerException;
import io.ids.argus.core.base.module.annotation.ArgusApplication;
import io.ids.argus.module.ArgusModule;

@ArgusApplication(pkg = "io.ids.argus.extension.modelchecking")
public class ModelCheckingApplication {

    public static void main(String[] args) throws
            InterruptedException,
            ArgusScannerException {
        ArgusModule.start(ModelCheckingApplication.class);
        Thread.currentThread().join();
    }
}
