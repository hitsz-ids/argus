package io.ids.argus.core.base.enviroment.context;

import io.ids.argus.core.base.module.controller.IArgusController;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;

/**
 * The Container of all the Argus Extension Controller
 */
class ArgusControllerClass {
    /**
     * The Clazz name.
     */
    final String className;
    /**
     * The Instance.
     */
    WeakReference<IArgusController> instance;

    /**
     * Gets clazz name.
     *
     * @return the clazz name
     */
    public String getClassName() {
        return className;
    }

    public ArgusControllerClass(String className) {
        this.className = className;
    }

    /**
     * Gets controller.
     *
     * @return the controller
     * @throws ClassNotFoundException    the class not found exception
     * @throws NoSuchMethodException     the no such method exception
     * @throws InvocationTargetException the invocation target exception
     * @throws InstantiationException    the instantiation exception
     * @throws IllegalAccessException    the illegal access exception
     */
    public IArgusController getController()
            throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        IArgusController object;
        if (instance == null) {
            object = createInstance();
            return object;
        }
        object = instance.get();
        if (object == null) {
            object = createInstance();
        }
        return object;
    }

    private IArgusController createInstance() throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, InstantiationException {
        var classLoader = Thread.currentThread().getContextClassLoader();
        Class<?> clazz = classLoader.loadClass(className);
        IArgusController controller = (IArgusController) clazz.getDeclaredConstructor().newInstance();
        instance = new WeakReference<>(controller);
        return instance.get();
    }
}
