package io.ids.argus.server.base.module.configuration;

import io.ids.argus.core.base.utils.Constants;
import io.ids.argus.core.base.utils.Security;
import io.ids.argus.core.conf.log.ArgusLogger;
import io.ids.argus.server.base.exception.error.URIError;
import io.ids.argus.server.base.exception.ArgusURIException;
import io.ids.argus.server.base.module.manager.ArgusModule;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

public final class ModuleConfiguration {
    private final ArgusLogger log = new ArgusLogger(ModuleConfiguration.class);
    private final List<ModuleEntity> supports = new ArrayList<>();
    private static final ReentrantLock lock = new ReentrantLock();
    public List<ModuleEntity> getModuleEntities() {
        return supports;
    }

    private final String modulePubDir;

    public ModuleConfiguration(String modulePubDir) {
        this.modulePubDir = modulePubDir;
    }

    public void initModules(String[] modules) {
        URI uri;
        String query;
        String[] versions = null;
        Scanner scanner;
        try {
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
                    throw new ArgusURIException(URIError.ERROR_PARSE_MODULE);
                }
                supports.add(new ModuleEntity(uri.getHost(), versions));
            }
        } catch (URISyntaxException e) {
            throw new ArgusURIException(URIError.ERROR_PARSE_MODULE);
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

    public ArgusModule validate(String secret, String version) {
        if (StringUtils.isEmpty(secret)) {
            return null;
        }
        var publicFile = modulePubDir;
        if (!publicFile.endsWith(Constants.FILE_SEPARATOR)) {
            publicFile += Constants.FILE_SEPARATOR;
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
                                Constants.FILE_SEPARATOR +
                                Constants.MODULE_PUB_FILE_NAME);
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

    private ArgusModule getArgusModule(ModuleEntity moduleConf,
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
                    new ArgusModule(moduleConf.getName(), version) : null;
        }
        return null;
    }
}
