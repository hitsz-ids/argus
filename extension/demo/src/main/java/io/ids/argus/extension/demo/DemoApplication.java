package io.ids.argus.extension.demo;

import io.ids.argus.core.exception.ArgusScannerException;
import io.ids.argus.core.module.annotation.ArgusApplication;
import io.ids.argus.module.ArgusClient;

import javax.net.ssl.SSLException;

@ArgusApplication(pkg = "io.ids.argus.extension.demo")
public class DemoApplication {

    public static void main(String[] args) throws SSLException, InterruptedException, ArgusScannerException {
        ArgusClient argusClient = new ArgusClient();
        argusClient.start(DemoApplication.class);
        Thread.currentThread().join();
    }
}
