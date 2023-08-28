package io.ids.argus.module.context;

import io.ids.argus.core.common.InvokerStatus;
import io.ids.argus.core.enviroment.invoker.Invoker;
import io.ids.argus.core.enviroment.invoker.InvokeOutput;
import io.ids.argus.core.exception.ArgusInvokerException;
import io.ids.argus.core.json.Transformer;
import io.ids.argus.core.module.IArgusController;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ModuleApiInvoker extends Invoker {
    public ModuleApiInvoker(Data data, IArgusController controller, Method method) {
        super(data, controller, method);
    }

    @Override
    public String invoke() throws InvocationTargetException, IllegalAccessException {
        boolean hasParams = hasParameter();
        Object result;
        if (!hasParams) {
            result = method.invoke(controller);
        } else {
            result = method.invoke(controller, initParams());
        }
        if (!(result instanceof InvokeOutput)) {
            throw new ArgusInvokerException(InvokerStatus.ERROR_INVOKE_RETURN);
        }
        return Transformer.toJsonString(result);
    }
}