package io.ids.argus.center.module;

import io.ids.argus.core.transport.grpc.ArgusModule;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class LockPool {
    private static final LockPool poll = new LockPool();
    private static final Map<ArgusModule, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    private LockPool() {
    }

    public static LockPool get() {
        return poll;
    }

    private ReentrantLock getLock(ArgusModule module) {
        var lock = lockMap.get(module);
        if (Objects.isNull(lock)) {
            lock = new ReentrantLock();
            lockMap.put(module, lock);
        }
        return lock;
    }

    public void lock(ArgusModule module) {
        getLock(module).lock();
    }

    public void unlock(ArgusModule module) {
        getLock(module).unlock();
    }
}
