package io.ids.argus.module.context;


import io.ids.argus.core.base.common.Namespace;
import io.ids.argus.core.base.enviroment.context.ArgusContext;
import io.ids.argus.core.base.enviroment.invoker.Invoker;
import io.ids.argus.core.base.module.IArgusController;

import java.lang.reflect.Method;

public class ModuleContext extends ArgusContext {

    @Override
    public Invoker initInvoker(Invoker.Data data, Namespace namespace, IArgusController controller, Method method) {
        if (namespace == Namespace.API) {
            return new ModuleApiInvoker(data, controller, method);
        } else if (namespace == Namespace.JOB) {
            return new ModuleJobInvoker(data, controller, method);
        }
        return null;
    }
}
