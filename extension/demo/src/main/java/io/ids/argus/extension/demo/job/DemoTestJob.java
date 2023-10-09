package io.ids.argus.extension.demo.job;

import io.ids.argus.core.base.json.Transformer;
import io.ids.argus.core.conf.log.ArgusLogger;
import io.ids.argus.job.client.AArgusJob;

import java.util.concurrent.TimeUnit;

public class DemoTestJob extends AArgusJob<DemoTestJobParams, DemoTestJobResult> {
    private final ArgusLogger log = new ArgusLogger(DemoTestJob.class);

    public DemoTestJob(String seq, String params) {
        super(seq, params);
    }

    @Override
    public DemoTestJobParams transform(String params) {
        return Transformer.fromJson(params, DemoTestJobParams.class);
    }

    @Override
    public void onStop() {
        log.debug("任务主动停止");
    }

    @Override
    public void onFailed() {
        log.debug("任务执行失败");
    }

    @Override
    public DemoTestJobResult onRun() {
        var i = 0;
        while (running()) {
            log.debug("demo任务启动 :" + i++);
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return new DemoTestJobResult();
    }

    @Override
    public void onComplete() {
        log.debug("demo任务执行完成");
    }
}
