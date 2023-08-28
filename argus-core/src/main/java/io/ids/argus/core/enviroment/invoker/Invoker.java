package io.ids.argus.core.enviroment.invoker;

import io.ids.argus.core.common.InvokerStatus;
import io.ids.argus.core.exception.ArgusInvokerException;
import io.ids.argus.core.json.ArgusJson;
import io.ids.argus.core.json.Transformer;
import io.ids.argus.core.module.IArgusController;
import lombok.Data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;

public abstract class Invoker {
    public record Data(String namespace, String url, ArgusJson params, ArgusJson customized) {
    }

    private final Data data;
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
            throw new ArgusInvokerException(InvokerStatus.ERROR_INVOKER_PARAMS);
        }
        ArgusJson customized = data.customized();
        if (!ArgusJson.isNull(customized)) {
            ((InvokeArgs) params).setCustomized(customized);
        }
        return params;
    }

}
