package io.ids.argus.core.base.module.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface API {
    /**
     * Specify the path to the API namespace
     *
     * @return url string
     */
    String url();
}
