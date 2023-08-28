package io.ids.argus.center.module;

import com.google.common.collect.Iterators;
import io.ids.argus.center.conf.CenterProperties;
import io.ids.argus.center.exception.ArgusURIException;
import io.ids.argus.center.service.Connection;
import io.ids.argus.core.conf.ArgusLogger;
import io.ids.argus.core.grpc.ArgusModule;
import io.ids.argus.core.utils.Security;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class ModuleManager {
    private final ArgusLogger log = new ArgusLogger(ModuleManager.class);
    private final List<Module> supports = new ArrayList<>();
    private static final Map<ArgusModule, Connection> loginMap = new ConcurrentHashMap<>();
    private static final ReentrantLock lock = new ReentrantLock();

    public static final ModuleManager instance = new ModuleManager();

    public List<Module> getModulesConf() {
        return supports;
    }

    public ArgusModule[] getLoginModules() {
        return Iterators.toArray(loginMap.keySet().iterator(), ArgusModule.class);
    }

    private static final String FILE_SEPARATOR = "/";
    private static final String MODULE_PUB_FILE_NAME = "module.pem";

    public static ModuleManager get() {
        return instance;
    }

    public void initModules(List<String> modules) throws URISyntaxException {
        URI uri;
        String query;
        String[] versions = null;
        Scanner scanner;
        for (String module : modules) {
            uri = new URI(module);
            query = uri.getQuery();
            scanner = new Scanner(query);
            while (scanner.hasNext()) {
                String pair = scanner.next();
                String[] keyValue = pair.split("=");
                String key = keyValue[0];
                String value = keyValue[1];
                if (Objects.equals(key, "version")) {
                    versions = value.split(";");
                    break;
                }
            }
            scanner.close();
            if (ArrayUtils.isEmpty(versions)) {
                throw new ArgusURIException("module配置参数错误,请配置对应的version版本号");
            }
            supports.add(new Module(uri.getHost(), versions));
        }
    }

    public boolean supportVersion(String name, String version) {
        lock.lock();
        try {
            for (var module : supports) {
                if (Objects.equals(module.getName(), name)) {
                    return module.support(version);
                }
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public boolean isLogin(ArgusModule module) {
        lock.lock();
        try {
            var connection = loginMap.get(module);
            return connection != null;
        } finally {
            lock.unlock();
        }
    }

    public Connection getConnection(String name, String version) {
        lock.lock();
        try {
            return loginMap.get(ArgusModule.newBuilder()
                    .setVersion(version)
                    .setName(name)
                    .build());
        } finally {
            lock.unlock();
        }
    }

    public boolean support(String name) {
        lock.lock();
        try {
            for (var module : supports) {
                if (Objects.equals(module.getName(), name)) {
                    return true;
                }
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public void login(ArgusModule module, Connection connection) {
        lock.lock();
        try {
            loginMap.putIfAbsent(module, connection);
        } finally {
            lock.unlock();
        }
    }

    public void logout(ArgusModule module) {
        lock.lock();
        try {
            loginMap.remove(module);
        } finally {
            lock.unlock();
        }
    }

    public ArgusModule validate(String secret, String version) {
        if (StringUtils.isEmpty(secret)) {
            return null;
        }
        var publicFile = CenterProperties.get().getModulesDir();
        if (!publicFile.endsWith(FILE_SEPARATOR)) {
            publicFile += FILE_SEPARATOR;
        }
        ArgusModule module = null;
        try {
            String name;
            Path pub;
            for (var argusModuleConf : supports) {
                name = argusModuleConf.getName();
                pub = Paths.get(
                        publicFile +
                                name +
                                FILE_SEPARATOR +
                                MODULE_PUB_FILE_NAME);
                module = getArgusModule(argusModuleConf, version, pub, secret);
                if (!Objects.isNull(module)) {
                    break;
                }
            }
        } catch (NoSuchAlgorithmException
                 | CertificateException
                 | InvalidKeySpecException
                 | InvalidKeyException
                 | SignatureException
                 | FileNotFoundException e) {
            log.error(e.getMessage(), e);
        }
        return module;
    }

    private ArgusModule getArgusModule(Module moduleConf,
                                       String version,
                                       Path pub,
                                       String secret) throws FileNotFoundException,
            CertificateException,
            NoSuchAlgorithmException,
            InvalidKeySpecException,
            SignatureException,
            InvalidKeyException {
        if (Files.exists(pub)) {
            var verify = Security.verify(pub.toString(), moduleConf.getName(), secret);
            return verify && moduleConf.support(version) ?
                    ArgusModule.newBuilder()
                            .setName(moduleConf.getName())
                            .setVersion(version)
                            .build() : null;
        }
        return null;
    }
}
