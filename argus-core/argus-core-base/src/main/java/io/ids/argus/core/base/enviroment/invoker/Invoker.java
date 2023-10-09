package io.ids.argus.core.base.enviroment.invoker;

import io.ids.argus.core.base.exception.ArgusInvokerException;
import io.ids.argus.core.base.json.ArgusJson;
import io.ids.argus.core.base.json.Transformer;
import io.ids.argus.core.base.module.controller.IArgusController;
import io.ids.argus.core.base.exception.error.InvokerError;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public abstract class Invoker {
    public record Data(String url, ArgusJson params, ArgusJson customized) {
    }

    protected final Data data;
    protected final Method method;
    protected final IArgusController controller;

    public Invoker(Data data, IArgusController controller, Method method) {
        this.data = data;
        this.method = method;
        this.controller = controller;
    }

    public abstract String invoke() throws InvocationTargetException, IllegalAccessException;

    protected final boolean hasParameter() {
        Parameter[] parameters = method.getParameters();
        return parameters.length > 0;
    }

    protected final Object initParams() {
        var parameters = method.getParameters();
        var parameter = parameters[0];
        Object params = Transformer.fromJson(data.params(), parameter.getParameterizedType());
        if (!(params instanceof InvokeArgs)) {
            throw new ArgusInvokerException(InvokerError.ERROR_INVOKER_PARAMS);
        }
        return params;
    }

}
