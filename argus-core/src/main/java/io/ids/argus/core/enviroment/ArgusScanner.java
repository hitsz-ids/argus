package io.ids.argus.core.enviroment;

import io.ids.argus.core.common.Namespace;
import io.ids.argus.core.common.ScanStatus;
import io.ids.argus.core.conf.ArgusLogger;
import io.ids.argus.core.exception.ArgusScannerException;
import io.ids.argus.core.module.annotation.API;
import io.ids.argus.core.module.annotation.ArgusApplication;
import io.ids.argus.core.module.annotation.ArgusController;
import io.ids.argus.core.module.annotation.Job;
import io.ids.argus.core.utils.Constant;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class ArgusScanner {
    private static final ArgusLogger log = new ArgusLogger(ArgusScanner.class);

    private final Map<String, ArgusControllerContext> contexts = new HashMap<>();

    /**
     * Gets method.
     *
     * @param url the url
     * @return the method
     */
    @Nullable
    public ArgusControllerMethod getCMethod(String url) {
        var context = contexts.get(url);
        if (Objects.isNull(context)) {
            return null;
        }
        return context.getMethod();
    }

    /**
     * Gets controller.
     *
     * @param url the url
     * @return the controller
     */
    @Nullable
    public ArgusControllerClass getCCLass(String url) {
        var context = contexts.get(url);
        if (Objects.isNull(context)) {
            return null;
        }
        return context.getClazz();
    }

    public void scan(Class<?> primarySource) throws ArgusScannerException {
        var argusApplication = primarySource.getAnnotation(ArgusApplication.class);
        if (argusApplication == null) {
            throw new ArgusScannerException(ScanStatus.SCAN_NOT_FOUND_APPLICATION_ERROR);
        }
        var pkg = argusApplication.pkg();
        if (StringUtils.isEmpty(pkg)) {
            throw new ArgusScannerException(ScanStatus.SCAN_NOT_FOUND_PKG_PATH_ERROR);
        }
        var classLoader = ArgusApplication.class.getClassLoader();
        try {
            Enumeration<URL> url = classLoader.getResources(
                    pkg.replace(Constant.CLASS_FILE_SEPARATOR, Constant.FILE_SEPARATOR));
            if (url == null) {
                throw new ArgusScannerException(ScanStatus.SCAN_PKG_ERROR);
            }
            while (url.hasMoreElements()) {
                var mUrl = url.nextElement();
                innerScan(mUrl, pkg);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new ArgusScannerException(ScanStatus.SCAN_JAR_ERROR);
        }
    }

    private void innerScan(URL url, String pkg) throws ArgusScannerException {
        String protocol = url.getProtocol();
        if (Objects.equals(Constant.JAR_FILE_SUFFIX, protocol)) {
            initContextFromJar(url, pkg, this::addContext);
        } else {
            try {
                var file = new File(url.toURI());
                initContextFromJar(file.listFiles(), pkg, this::addContext);
            } catch (URISyntaxException e) {
                throw new ArgusScannerException(ScanStatus.SCAN_NOT_FOUND_RES_ERROR);
            }
        }
    }

    private void initContextFromJar(URL url, String pkg, Consumer<Class<?>> action) {
        try {
            JarURLConnection connection = (JarURLConnection) url.openConnection();
            if (Objects.isNull(connection)) {
                throw new ArgusScannerException(ScanStatus.GET_URL_ERROR);
            }
            var jarFile = connection.getJarFile();
            if (Objects.isNull(jarFile)) {
                throw new ArgusScannerException(ScanStatus.GET_JAR_ERROR);
            }
            var jarEntryEnumeration = jarFile.entries();
            while (jarEntryEnumeration.hasMoreElements()) {
                var entry = jarEntryEnumeration.nextElement();
                var jarEntryName = entry.getName();
                if (!jarEntryName.endsWith(Constant.CLASS_FILE_SUFFIX)) {
                    // 跳过所有非class的文件
                    continue;
                }
                if (jarEntryName
                        .replace(Constant.FILE_SEPARATOR, Constant.CLASS_FILE_SEPARATOR)
                        .startsWith(pkg)) {
                    // 只获取当前包下面的类
                    String className = jarEntryName
                            .substring(0, jarEntryName.lastIndexOf(Constant.CLASS_FILE_SEPARATOR))
                            .replace(Constant.FILE_SEPARATOR, Constant.CLASS_FILE_SEPARATOR);
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isAnnotationPresent(ArgusController.class)) {
                        action.accept(clazz);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void addContext(Class<?> clazz) {
        Method[] declaredMethods = clazz.getDeclaredMethods();
        var cClass = new ArgusControllerClass(clazz.getName());
        API api;
        Job job;
        for (Method method : declaredMethods) {
            api = method.getAnnotation(API.class);
            job = method.getAnnotation(Job.class);
            if (!Objects.isNull(api) && !Objects.isNull(job)) {
                log.error("{}的[{}]方法不能同时配置@API、@Job",
                        cClass.getClassName(),
                        method.getName());
                return;
            }

            String namespace;
            String url;
            if (!Objects.isNull(job)) {
                namespace = Namespace.JOB.getName();
                url = job.url();
            } else if (!Objects.isNull(api)) {
                namespace = Namespace.API.getName();
                url = api.url();
            } else {
                log.error("{}的[{}]方法未指定注解类，请检查！",
                        cClass.getClassName(),
                        method.getName());
                continue;
            }
            if (StringUtils.isNotBlank(url)) {
                url = resolveUrl(url);
                var cMethod = resolveMethod(
                        url,
                        method,
                        clazz.getName(),
                        namespace,
                        method.getParameterTypes());
                url = namespace + url;
                contexts.put(url, new ArgusControllerContext(cClass, cMethod));
            }
        }
    }

    protected String resolveUrl(String url) {
        url = url.toLowerCase();
        if (!url.startsWith(Constant.URL_SEPARATOR)) {
            url = Constant.URL_SEPARATOR + url;
        }
        if (!url.endsWith(Constant.URL_SEPARATOR)) {
            url = url + Constant.URL_SEPARATOR;
        }
        return url;
    }

    private ArgusControllerMethod resolveMethod(String url, Method method,
                                                String className,
                                                String namespace,
                                                Class<?>[] parameterTypes) {
        var mMethod = new ArgusControllerMethod();
        mMethod.setMethodName(method.getName());
        mMethod.setUrl(url);
        mMethod.setClazzName(className);
        mMethod.setNamespace(namespace);
        mMethod.setParameterTypes(parameterTypes);
        return mMethod;
    }

    private void initContextFromJar(File[] files, String pkg, Consumer<Class<?>> action) {
        if (ArrayUtils.isEmpty(files)) {
            return;
        }
        Class<?> clazz;
        for (File file : files) {
            if (file.isDirectory()) {
                File[] childFiles = file.listFiles();
                initContextFromJar(childFiles,
                        pkg + Constant.CLASS_FILE_SEPARATOR + file.getName(), action);
            } else if (file.getName().endsWith(Constant.CLASS_FILE_SUFFIX)) {
                try {
                    clazz = Thread.currentThread().getContextClassLoader().loadClass(
                            pkg +
                                    Constant.CLASS_FILE_SEPARATOR +
                                    file.getName().replace(Constant.CLASS_FILE_SUFFIX
                                            , ""));
                    if (clazz.isAnnotationPresent(ArgusController.class)) {
                        action.accept(clazz);
                    }
                } catch (ClassNotFoundException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }
}
