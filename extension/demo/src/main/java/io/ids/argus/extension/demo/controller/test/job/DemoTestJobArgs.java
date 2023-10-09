package io.ids.argus.extension.demo.controller.test.job;

import io.ids.argus.core.base.enviroment.invoker.InvokeArgs;
import lombok.Data;

@Data
public class DemoTestJobArgs implements InvokeArgs {
    private String name;
}
