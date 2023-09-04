package io.ids.argus.extension.demo;


import io.ids.argus.core.base.module.API;
import io.ids.argus.core.base.module.ArgusController;
import io.ids.argus.core.base.module.IArgusController;
import io.ids.argus.core.base.module.Job;

@ArgusController
public class DemoController implements IArgusController {

    @API(url = "test")
    public void test() {
        System.out.println("test");
    }

    @API(url = "test/returns")
    public HelloWorld test1() {
        return new HelloWorld();
    }

    @Job(url = "test/job")
    public TestJob jobTest() {
        return new TestJob();
    }
}
