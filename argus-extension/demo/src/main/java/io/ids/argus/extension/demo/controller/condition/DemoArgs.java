package io.ids.argus.extension.demo.controller.condition;

import io.ids.argus.core.base.enviroment.invoker.IInvokeArgs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DemoArgs implements IInvokeArgs {
    private String a;
}
