package io.ids.argus.job;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public interface AbstractJob<P extends IJobParams, R extends IJobResult> {

    void onCreate(P params);
    void onStart();

    void onStop();

    void onFailed();

    R onComplete();
}
