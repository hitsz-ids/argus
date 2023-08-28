package io.ids.argus.module.context;

import io.ids.argus.core.enviroment.invoker.Invoker;
import io.ids.argus.core.module.IArgusController;

import java.lang.reflect.Method;

public class ModuleJobInvoker extends Invoker {

    public ModuleJobInvoker(Data params, IArgusController controller, Method method) {
        super(params, controller, method);
    }

    @Override
    public String invoke() {
        return null;
    }
}
