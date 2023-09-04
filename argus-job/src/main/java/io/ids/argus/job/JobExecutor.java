package io.ids.argus.job;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class JobExecutor {
    private final ThreadPoolExecutor service;

    private static final int CORE_POOL_SIZE = 10;
    private static final int MAX_POOL_SIZE = 50;
    private static final int MAX_QUEUE_SIZE = 200;
    private static final int KEEP_ALIVE_TIME = 2;
    private static final JobExecutor instance = new JobExecutor();

    private JobExecutor() {
        service = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.MINUTES,
                new LinkedBlockingQueue<>(MAX_QUEUE_SIZE),
                r -> new Thread(r, "Argus-pool-worker-" + r.hashCode()),
                new ThreadPoolExecutor.AbortPolicy());
        service.allowCoreThreadTimeOut(true);
    }

    public static ThreadPoolExecutor get() {
        return instance.service;
    }

}
