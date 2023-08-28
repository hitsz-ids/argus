package io.ids.argus.core.enviroment.invoker;

import io.ids.argus.core.json.ArgusJson;

public abstract class InvokeArgs {
    private ArgusJson customized;

    public ArgusJson getCustomized() {
        return customized;
    }

    void setCustomized(ArgusJson customized) {
        this.customized = customized;
    }
}
