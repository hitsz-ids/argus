package io.ids.argus.module.context;

import io.ids.argus.core.base.common.InvokerStatus;
import io.ids.argus.core.base.enviroment.invoker.Invoker;
import io.ids.argus.core.base.exception.ArgusInvokerException;
import io.ids.argus.core.base.json.Transformer;
import io.ids.argus.core.base.module.IArgusController;
import io.ids.argus.job.AbstractJob;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ModuleJobInvoker extends Invoker {

    public ModuleJobInvoker(Data params, IArgusController controller, Method method) {
        super(params, controller, method);
    }

    @Override
    public String invoke() throws InvocationTargetException,
            IllegalAccessException {
        boolean hasParams = hasParameter();
        Object result;
        if (!hasParams) {
            result = method.invoke(controller);
        } else {
            result = method.invoke(controller, initParams());
        }
        if (!(result instanceof AbstractJob<?,?>)) {
            throw new ArgusInvokerException(InvokerStatus.ERROR_INVOKE_JOB_RETURN);
        }
        return Transformer.toJsonString(result);
    }
}
