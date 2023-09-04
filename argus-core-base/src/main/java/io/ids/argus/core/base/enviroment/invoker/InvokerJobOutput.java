package io.ids.argus.core.base.enviroment.invoker;

import io.ids.argus.core.base.json.ArgusJson;

public class InvokerJobOutput implements InvokeOutput {
    private ArgusJson customized;

    public ArgusJson getCustomized() {
        return customized;
    }

    void setCustomized(ArgusJson customized) {
        this.customized = customized;
    }
}
