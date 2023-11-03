package io.ids.argus.entry;

import io.ids.argus.center.startup.Argus;
import io.ids.argus.core.base.application.BaseApplication;
import io.ids.argus.core.base.module.annotation.ArgusApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@ArgusApplication(pkg = "io.ids.argus.entry")
public class Application extends BaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        Argus.start(Application.class, BaseApplication::boot);
    }
}
