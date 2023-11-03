package io.ids.argus.job.server;

import io.ids.argus.core.base.application.BaseApplication;

import java.io.IOException;

public class JobApplication extends BaseApplication {
    public static void main(String[] args) {
        try {
            var bootstrap = new Bootstrap();
            bootstrap.start(BaseApplication::boot);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
