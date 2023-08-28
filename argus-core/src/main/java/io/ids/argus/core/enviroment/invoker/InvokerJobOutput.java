package io.ids.argus.core.enviroment.invoker;

import io.ids.argus.core.json.ArgusJson;

public class InvokerJobOutput implements InvokeOutput {
    private ArgusJson customized;

    public ArgusJson getCustomized() {
        return customized;
    }

    void setCustomized(ArgusJson customized) {
        this.customized = customized;
    }
}
