package io.ids.argus.module.context;


import io.ids.argus.core.base.enviroment.context.ArgusContext;
import io.ids.argus.core.base.enviroment.invoker.Invoker;
import io.ids.argus.core.base.module.controller.IArgusController;
import io.ids.argus.module.controller.InternalController;

import java.lang.reflect.Method;

public class ModuleContext extends ArgusContext {

    @Override
    public Invoker initInvoker(Invoker.Data data, IArgusController controller, Method method) {
        return new ModuleApiInvoker(data, controller, method);
    }
    public void addInternalContext() {
        addContext(InternalController.class);
    }
}
