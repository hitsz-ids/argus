package io.ids.argus.core.base.module.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ArgusApplication {
    String pkg();
}
