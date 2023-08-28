package io.ids.argus.core.module.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface API {
    /**
     * 指定API命名空间的路径
     *
     * @return url string
     */
    String url();
}
