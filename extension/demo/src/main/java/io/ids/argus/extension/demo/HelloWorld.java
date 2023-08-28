package io.ids.argus.extension.demo;

import io.ids.argus.core.enviroment.invoker.InvokeOutput;
import lombok.Data;

@Data
public class HelloWorld implements InvokeOutput {
    private final String name = "hello world";
}
