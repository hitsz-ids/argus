package io.ids.argus.core.base.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Job {
    /**
     * 指定JOB命名空间的路径
     *
     * @return url string
     */
    String url();
}
