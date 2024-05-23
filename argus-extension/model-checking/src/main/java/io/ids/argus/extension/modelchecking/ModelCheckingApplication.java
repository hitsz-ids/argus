package io.ids.argus.extension.modelchecking;

import io.ids.argus.core.base.exception.ArgusScannerException;
import io.ids.argus.core.base.module.annotation.ArgusApplication;
import io.ids.argus.module.ArgusModule;
import io.ids.argus.store.client.ArgusStore;

import java.io.IOException;

@ArgusApplication(pkg = "io.ids.argus.extension.modelchecking")
public class ModelCheckingApplication {

    public static void main(String[] args) throws
            InterruptedException,
            ArgusScannerException, IOException {
        ArgusStore.init();
        ArgusModule.start(ModelCheckingApplication.class);
        Thread.currentThread().join();
    }
}
