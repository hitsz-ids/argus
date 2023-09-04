package io.ids.argus.extension.demo;

import io.ids.argus.job.AbstractJob;

public class TestJob implements AbstractJob<TestJobParams, TestJobResult> {
    @Override
    public void onCreate(TestJobParams params) {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onFailed() {

    }

    @Override
    public TestJobResult onComplete() {
        return null;
    }
}
