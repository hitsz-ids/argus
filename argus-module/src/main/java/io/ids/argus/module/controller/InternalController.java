package io.ids.argus.module.controller;

import io.ids.argus.core.base.module.annotation.API;
import io.ids.argus.core.base.module.annotation.ArgusController;
import io.ids.argus.core.base.module.controller.IArgusController;
import io.ids.argus.job.client.ArgusJob;
import io.ids.argus.module.controller.args.StopJobArg;
import io.ids.argus.module.controller.outputs.StopJobOutput;

@ArgusController
public class InternalController implements IArgusController {
    @API(url = "'stop-job'")
    public StopJobOutput stop(StopJobArg request) {
        var res = ArgusJob.get().stop(request.getSeq());
        return new StopJobOutput(res.getCode().getNumber(), res.getMsg());
    }
}
