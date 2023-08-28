package io.ids.argus.core.enviroment;

class ArgusControllerContext {
    private final ArgusControllerClass clazz;
    private final ArgusControllerMethod method;

    public ArgusControllerContext(ArgusControllerClass clazz, ArgusControllerMethod method) {
        this.clazz = clazz;
        this.method = method;
    }


    public ArgusControllerClass getClazz() {
        return clazz;
    }

    public ArgusControllerMethod getMethod() {
        return method;
    }
}
