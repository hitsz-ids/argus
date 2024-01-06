package io.ids.argus.extension.demo.controller;


import io.ids.argus.core.base.module.annotation.API;
import io.ids.argus.core.base.module.annotation.ArgusController;
import io.ids.argus.core.base.module.controller.IArgusController;
import io.ids.argus.extension.demo.controller.condition.DemoArgs;
import io.ids.argus.extension.demo.controller.condition.job.DemoTestJobArgs;
import io.ids.argus.extension.demo.controller.condition.job.DemoTestJobEntity;
import io.ids.argus.extension.demo.controller.condition.returns.DemoReturnsOutput;
import io.ids.argus.extension.demo.job.DemoTestJobParams;

@ArgusController
public class DemoController implements IArgusController {

    @API(url = "test")
    public void test(DemoArgs params) {
        System.out.println(params.getA());
    }

    @API(url = "test/returns")
    public DemoReturnsOutput test1() {
        return new DemoReturnsOutput();
    }

    @API(url = "test/job")
    public DemoTestJobEntity test2(DemoTestJobArgs args) {
        var params = new DemoTestJobParams();
        params.setJobName(args.getName());
        return new DemoTestJobEntity(params);
    }
}
