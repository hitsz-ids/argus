package io.ids.argus.job;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public final class ArgusRunnable<P extends IJobParams, R extends IJobResult> implements Runnable {
    private final AbstractJob<P, R> job;
    private Status status;
    private final ReentrantLock lock = new ReentrantLock();

    private final Condition condition = lock.newCondition();

    public ArgusRunnable(AbstractJob<P, R> job) {
        this.job = job;
    }

    @Override
    public final void run() {
        status = Status.RUNNING;
        job.onStart();
    }

    public void stop() {
        lock.lock();
        try {
            if (status != Status.RUNNING) {
                return;
            }
            status = Status.STOPPED;
            job.onStop();
        } finally {
            lock.unlock();
        }
    }

    public void failed() {
        lock.lock();
        try {
            status = Status.FAILED;
            job.onFailed();
        } finally {
            lock.unlock();
        }
    }

    protected void pause() throws InterruptedException {
        lock.lock();
        try {
            condition.await();
        } finally {
            lock.unlock();
        }
    }
}
