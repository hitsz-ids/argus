package io.ids.argus.extension.demo.controller.test.returns;

import io.ids.argus.core.base.enviroment.invoker.InvokeOutput;
import lombok.Data;

@Data
public class DemoReturnsOutput implements InvokeOutput {
    private final String name = "hello world";
}
