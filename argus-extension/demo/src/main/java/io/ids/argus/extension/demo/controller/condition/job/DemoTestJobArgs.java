package io.ids.argus.extension.demo.controller.condition.job;

import io.ids.argus.core.base.enviroment.invoker.IInvokeArgs;
import lombok.Data;

@Data
public class DemoTestJobArgs implements IInvokeArgs {
    private String name;
}
