package io.ids.argus.core.base.enviroment.context;

import io.ids.argus.core.base.enviroment.invoker.Invoker;
import io.ids.argus.core.base.exception.ArgusInvokerException;
import io.ids.argus.core.base.module.controller.IArgusController;
import io.ids.argus.core.base.exception.error.InvokerError;
import io.ids.argus.core.conf.log.ArgusLogger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * The Context of Argus
 */
public abstract class ArgusContext extends ArgusScanner {

    private final ArgusLogger log = new ArgusLogger(ArgusContext.class);

    public boolean contains(String url) {
        ArgusControllerClass cc = getCCLass(url);
        ArgusControllerMethod cm = getCMethod(url);
        return Objects.nonNull(cc) && Objects.nonNull(cm);
    }

//    public boolean special(String url) {
//        if (STOP_JOB_URL.equals(url)) {
//            return true;
//        }
//        return false;
//    }

    public final String invoke(Invoker.Data data) {
        return innerInvoke(data);
    }

    /**
     * Do invoke（execute json method）
     *
     * @param data Invoke params
     * @return result string
     * @throws ArgusInvokerException Invoker Exception
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
        } catch (ArgusInvokerException e) {
            throw e;
        } catch (Exception e) {
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
