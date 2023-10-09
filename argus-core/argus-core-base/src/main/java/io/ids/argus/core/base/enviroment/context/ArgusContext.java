package io.ids.argus.core.base.enviroment.context;

import io.ids.argus.core.base.enviroment.invoker.Invoker;
import io.ids.argus.core.base.exception.ArgusInvokerException;
import io.ids.argus.core.base.module.controller.IArgusController;
import io.ids.argus.core.base.exception.error.InvokerError;
import io.ids.argus.core.conf.log.ArgusLogger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public abstract class ArgusContext extends ArgusScanner {

    private static final String STOP_JOB_URL = "/'stop-job'/";
    private final ArgusLogger log = new ArgusLogger(ArgusContext.class);

    public boolean contains(String url) {
        ArgusControllerClass cc = getCCLass(url);
        ArgusControllerMethod cm = getCMethod(url);
        return Objects.nonNull(cc) && Objects.nonNull(cm);
    }

    public boolean special(String url) {
        if (STOP_JOB_URL.equals(url)) {
            return true;
        }
        return false;
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
        var url = data.url();
        var cc = getCCLass(url);
        var cm = getCMethod(url);
        if (cc == null || cm == null) {
            throw new ArgusInvokerException(InvokerError.NOT_FOUND_URL, "[%s]", url);
        }
        Invoker invoker;
        try {
            invoker = initInvoker(data, cc.getController(), initMethod(cc, cm));
            if (Objects.isNull(invoker)) {
                throw new ArgusInvokerException(InvokerError.NOT_FOUND_INVOKER);
            }
            return invoker.invoke();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ArgusInvokerException(InvokerError.ERROR_INVOKE);
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
                                        IArgusController controller,
                                        Method method);
}
