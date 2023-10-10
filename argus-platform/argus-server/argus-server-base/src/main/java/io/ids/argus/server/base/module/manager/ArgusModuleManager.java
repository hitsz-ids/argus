package io.ids.argus.server.base.module.manager;

import io.ids.argus.core.conf.log.ArgusLogger;
import io.ids.argus.core.base.utils.Constants;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class ArgusModuleManager {
    private final ArgusLogger log = new ArgusLogger(ArgusModuleManager.class);
    private static final Map<String, ArgusModule> loginMap = new ConcurrentHashMap<>();
    private static final ReentrantLock lock = new ReentrantLock();
    public static final ArgusModuleManager instance = new ArgusModuleManager();

    private ArgusModuleManager() {
    }

    public static ArgusModuleManager get() {
        return instance;
    }

    public boolean isLogin(ArgusModule module) {
        lock.lock();
        try {
            var connection = loginMap.get(getModuleKey(module));
            return connection != null;
        } finally {
            lock.unlock();
        }
    }

    public ArgusModule getModule(String name, String version) {
        lock.lock();
        try {
            return loginMap.get(getModuleKey(name, version));
        } finally {
            lock.unlock();
        }
    }

    public void login(ArgusModule module) {
        lock.lock();
        try {
            loginMap.putIfAbsent(getModuleKey(module), module);
        } finally {
            lock.unlock();
        }
    }

    public void logout(ArgusModule module) {
        lock.lock();
        try {
            loginMap.remove(getModuleKey(module));
        } finally {
            lock.unlock();
        }
    }

    private String getModuleKey(ArgusModule module) {
        return getModuleKey(module.getName(), module.getVersion());
    }

    private String getModuleKey(String name, String version) {
        return name + Constants.MODULE_KEY_SEPARATOR + version;
    }
}
