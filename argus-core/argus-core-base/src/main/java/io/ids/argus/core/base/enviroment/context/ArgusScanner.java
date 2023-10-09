package io.ids.argus.core.base.enviroment.context;

import io.ids.argus.core.base.exception.error.ScanError;
import io.ids.argus.core.base.exception.ArgusScannerException;
import io.ids.argus.core.base.module.annotation.API;
import io.ids.argus.core.base.module.annotation.ArgusApplication;
import io.ids.argus.core.base.module.annotation.ArgusController;
import io.ids.argus.core.base.utils.Constants;
import io.ids.argus.core.base.utils.Utils;
import io.ids.argus.core.conf.log.ArgusLogger;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

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
            throw new ArgusScannerException(ScanError.SCAN_NOT_FOUND_APPLICATION_ERROR);
        }
        var pkg = argusApplication.pkg();
        if (StringUtils.isEmpty(pkg)) {
            throw new ArgusScannerException(ScanError.SCAN_NOT_FOUND_PKG_PATH_ERROR);
        }
        var classLoader = ArgusApplication.class.getClassLoader();
        try {
            Enumeration<URL> url = classLoader.getResources(
                    pkg.replace(Constants.CLASS_FILE_SEPARATOR, Constants.FILE_SEPARATOR));
            if (url == null) {
                throw new ArgusScannerException(ScanError.SCAN_PKG_ERROR);
            }
            while (url.hasMoreElements()) {
                var mUrl = url.nextElement();
                innerScan(mUrl, pkg);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new ArgusScannerException(ScanError.SCAN_JAR_ERROR);
        }
    }

    private void innerScan(URL url, String pkg) throws ArgusScannerException {
        String protocol = url.getProtocol();
        if (Objects.equals(Constants.JAR_FILE_SUFFIX, protocol)) {
            initContextFromUrl(url, pkg, this::addContext);
        } else {
            try {
                var file = new File(url.toURI());
                initContextFromFiles(file.listFiles(), pkg, this::addContext);
            } catch (URISyntaxException e) {
                throw new ArgusScannerException(ScanError.SCAN_NOT_FOUND_RES_ERROR);
            }
        }
    }

    private void initContextFromUrl(URL url, String pkg, Consumer<Class<?>> action) {
        try {
            JarURLConnection connection = (JarURLConnection) url.openConnection();
            if (Objects.isNull(connection)) {
                throw new ArgusScannerException(ScanError.GET_URL_ERROR);
            }
            var jarFile = connection.getJarFile();
            if (Objects.isNull(jarFile)) {
                throw new ArgusScannerException(ScanError.GET_JAR_ERROR);
            }
            var jarEntryEnumeration = jarFile.entries();
            while (jarEntryEnumeration.hasMoreElements()) {
                var entry = jarEntryEnumeration.nextElement();
                var jarEntryName = entry.getName();
                if (!jarEntryName.endsWith(Constants.CLASS_FILE_SUFFIX)) {
                    // 跳过所有非class的文件
                    continue;
                }
                if (jarEntryName
                        .replace(Constants.FILE_SEPARATOR, Constants.CLASS_FILE_SEPARATOR)
                        .startsWith(pkg)) {
                    // 只获取当前包下面的类
                    String className = jarEntryName
                            .substring(0, jarEntryName.lastIndexOf(Constants.CLASS_FILE_SEPARATOR))
                            .replace(Constants.FILE_SEPARATOR, Constants.CLASS_FILE_SEPARATOR);
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
        for (Method method : declaredMethods) {
            api = method.getAnnotation(API.class);
            String url;
            if (!Objects.isNull(api)) {
                url = api.url();
            } else {
                continue;
            }
            if (StringUtils.isNotBlank(url)) {
                url = Utils.pack(url);
                var cMethod = resolveMethod(
                        url,
                        method,
                        clazz.getName(),
                        method.getParameterTypes());
                contexts.put(url, new ArgusControllerContext(cClass, cMethod));
            }
        }
    }

    private ArgusControllerMethod resolveMethod(String url, Method method,
                                                String className,
                                                Class<?>[] parameterTypes) {
        var mMethod = new ArgusControllerMethod();
        mMethod.setMethodName(method.getName());
        mMethod.setUrl(url);
        mMethod.setClazzName(className);
        mMethod.setParameterTypes(parameterTypes);
        return mMethod;
    }

    private void initContextFromFiles(File[] files, String pkg, Consumer<Class<?>> action) {
        if (ArrayUtils.isEmpty(files)) {
            return;
        }
        Class<?> clazz;
        for (File file : files) {
            if (file.isDirectory()) {
                File[] childFiles = file.listFiles();
                initContextFromFiles(childFiles,
                        pkg + Constants.CLASS_FILE_SEPARATOR + file.getName(), action);
            } else if (file.getName().endsWith(Constants.CLASS_FILE_SUFFIX)) {
                try {
                    clazz = Thread.currentThread().getContextClassLoader().loadClass(
                            pkg +
                                    Constants.CLASS_FILE_SEPARATOR +
                                    file.getName().replace(Constants.CLASS_FILE_SUFFIX
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
