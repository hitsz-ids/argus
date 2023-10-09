package io.ids.argus.extension.demo.controller.test;

import io.ids.argus.core.base.enviroment.invoker.InvokeArgs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DemoArgs implements InvokeArgs {
    private String a;
}
