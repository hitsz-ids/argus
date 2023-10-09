package io.ids.argus.job.server.manager;

import io.ids.argus.job.server.error.CallbackError;
import io.ids.argus.job.server.exception.ArgusJobServerCallbackException;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

public class JobManager extends Thread {
    private static final int CORE_POOL_SIZE = 50;
    private static final int MAX_POOL_SIZE = 500;
    private static final int KEEP_ALIVE_TIME = 5;
    private static final JobManager instance = new JobManager();
    private final BlockingQueue<Job> queue = new LinkedBlockingQueue<>(100);
    private final ThreadPoolExecutor executor;
    private final Map<String, Job> executingJobs = new ConcurrentHashMap<>();

    private JobManager() {
        executor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.MINUTES,
                new LinkedBlockingQueue<>(1),
                new ThreadPoolExecutor.CallerRunsPolicy());
        start();
    }

    public static JobManager get() {
        return instance;
    }

    public boolean execute(Job.JobData jobdata) {
        var job = new Job(jobdata);
        return queue.offer(job);
    }

    public boolean remove(String seq) {
        return queue.remove(new Job(seq));
    }

    public boolean publishCommand(String seq, Command command) {
        var job = executingJobs.get(seq);
        if (Objects.isNull(job)) {
            throw new ArgusJobServerCallbackException(CallbackError.ERROR_NOT_FOUND_JOB);
        }
        return job.publishCommand(command);
    }

    public void unBind(Job job) {
        executingJobs.remove(job.seq());
    }

    public void bind(Job job) {
        executingJobs.put(job.seq(), job);
    }

    public boolean contains(String seq) {
        return executingJobs.containsKey(seq);
    }

    @Override
    public void run() {
        while (true) {
            try {
                var job = queue.take();
                execute(job);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    private void execute(Job job) {
        executor.execute(job);
    }
}
