package io.ids.argus.core.base.enviroment.context;

import io.ids.argus.core.base.enviroment.invoker.Invoker;
import io.ids.argus.core.base.exception.ArgusInvokerException;
import io.ids.argus.core.base.module.IArgusController;
import io.ids.argus.core.base.common.InvokerStatus;
import io.ids.argus.core.base.common.Namespace;
import io.ids.argus.core.base.conf.ArgusLogger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public abstract class ArgusContext extends ArgusScanner {
    private final ArgusLogger log = new ArgusLogger(ArgusContext.class);

    public boolean contains(String url, String namespace) {
        url = getRealUrl(url, namespace);
        ArgusControllerClass cc = getCCLass(url);
        ArgusControllerMethod cm = getCMethod(url);
        return Objects.nonNull(cc) && Objects.nonNull(cm);
    }

    private String getRealUrl(String url, String namespace) {
        var mNamespace = Namespace.get(namespace);
        if (mNamespace == null) {
            throw new ArgusInvokerException(InvokerStatus.NOT_FOUND_NAMESPACE);
        }
        return namespace + resolveUrl(url);
    }

    public final String invoke(Invoker.Data data) {
        return innerInvoke(data);
    }

    /**
     * 执行反射调用（执行json的方法）
     *
     * @param data 参数内容
     * @return 执行结果 string
     * @throws ArgusInvokerException 抛出调用错误的异常
     */
    private String innerInvoke(Invoker.Data data) throws ArgusInvokerException {

        var url = getRealUrl(data.url(), data.namespace());
        var cc = getCCLass(url);
        var cm = getCMethod(url);
        if (cc == null || cm == null) {
            throw new ArgusInvokerException(InvokerStatus.NOT_FOUND_URL, "[%s]", url);
        }
        var namespace = Namespace.get(data.namespace());
        Invoker invoker;
        try {
            invoker = initInvoker(data, namespace, cc.getController(), initMethod(cc, cm));
            if (Objects.isNull(invoker)) {
                throw new ArgusInvokerException(InvokerStatus.NOT_FOUND_INVOKER);
            }
            return invoker.invoke();
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                 InstantiationException |
                 IllegalAccessException e) {
            log.error(e.getMessage(), e);
            throw new ArgusInvokerException(InvokerStatus.ERROR_INVOKE);
        }
    }

    protected Method initMethod(ArgusControllerClass cc,
                                ArgusControllerMethod cm)
            throws NoSuchMethodException,
            ClassNotFoundException,
            InstantiationException,
            IllegalAccessException,
            InvocationTargetException {
        IArgusController controller = cc.getController();
        Class<?> clazz = controller.getClass();
        return clazz.getDeclaredMethod(cm.methodName, cm.getParameterTypes());
    }

    public abstract Invoker initInvoker(Invoker.Data data,
                                        Namespace namespace,
                                        IArgusController controller,
                                        Method method);
}
