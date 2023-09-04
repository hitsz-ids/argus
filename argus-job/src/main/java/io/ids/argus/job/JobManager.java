package io.ids.argus.job;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JobManager {
    private final Map<String, ArgusRunnable<?, ?>> runningJobs = new ConcurrentHashMap<>();

    public <P extends IJobParams, R extends IJobResult> void execute(
            String seq,
            Class<? extends AbstractJob<P, R>> job) throws NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        var runnable = new ArgusRunnable<>(job.getConstructor().newInstance());
        runningJobs.put(seq, runnable);
        JobExecutor.get().execute(runnable);
    }
}
