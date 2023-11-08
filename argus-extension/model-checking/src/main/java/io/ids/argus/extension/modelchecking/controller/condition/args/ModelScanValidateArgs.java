package io.ids.argus.extension.modelchecking.controller.condition.args;

import io.ids.argus.core.base.enviroment.invoker.IInvokeArgs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelScanValidateArgs implements IInvokeArgs {
    private String path;
}
