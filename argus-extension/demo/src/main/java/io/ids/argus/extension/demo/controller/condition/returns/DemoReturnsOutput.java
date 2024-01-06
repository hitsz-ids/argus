package io.ids.argus.extension.demo.controller.condition.returns;

import io.ids.argus.core.base.enviroment.invoker.IInvokeOutput;
import lombok.Data;

@Data
public class DemoReturnsOutput implements IInvokeOutput {
    private final String name = "hello world";
}
