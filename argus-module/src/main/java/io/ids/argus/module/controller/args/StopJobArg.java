package io.ids.argus.module.controller.args;

import io.ids.argus.core.base.enviroment.invoker.IInvokeArgs;

public class StopJobArg implements IInvokeArgs {
    private String seq;

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }
}
