package io.ids.argus.module.context;


import io.ids.argus.core.base.exception.error.InvokerError;
import io.ids.argus.core.base.enviroment.invoker.InvokeOutput;
import io.ids.argus.core.base.enviroment.invoker.Invoker;
import io.ids.argus.core.base.exception.ArgusInvokerException;
import io.ids.argus.core.base.json.ArgusJson;
import io.ids.argus.core.base.json.Transformer;
import io.ids.argus.core.base.module.controller.IArgusController;
import io.ids.argus.job.client.job.JobEntity;
import io.ids.argus.module.ArgusModule;
import io.ids.argus.module.utils.ModuleConstants;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

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
        if (Objects.equals(method.getReturnType(), void.class)) {
            return Transformer.toJsonString(ModuleConstants.EMPTY_STR);
        }
        if ((result instanceof JobEntity<?,?> job)) {
            var seq = ArgusModule.get().commit(job);
            var json = new ArgusJson();
            json.add("seq", seq);
            return json.toJsonString();
        }
        if (!(result instanceof InvokeOutput)) {
            throw new ArgusInvokerException(InvokerError.ERROR_INVOKE_RETURN);
        }
        return Transformer.toJsonString(result);
    }
}