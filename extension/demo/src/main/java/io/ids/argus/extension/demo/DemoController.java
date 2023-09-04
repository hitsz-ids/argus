package io.ids.argus.extension.demo;

import io.ids.argus.core.enviroment.invoker.EmptyArgs;
import io.ids.argus.core.enviroment.invoker.EmptyOutput;
import io.ids.argus.core.module.IArgusController;
import io.ids.argus.core.module.annotation.API;
import io.ids.argus.core.module.annotation.ArgusController;

@ArgusController
public class DemoController implements IArgusController {

    @API(url = "test")
    public void test() {
        System.out.println("test");
    }

    @API(url = "test/returns")
    public HelloWorld test1(EmptyArgs params) {
        return new HelloWorld();
    }
}
