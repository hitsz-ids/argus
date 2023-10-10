package io.ids.argus.core.base.enviroment.context;

class ArgusControllerMethod {
    /**
     * The Url.
     */
    String url;
    /**
     * The Method name.
     */
    String methodName;
    /**
     * The Clazz name.
     */
    String clazzName;
    /**
     * The Parameter types.
     */
    Class<?>[] parameterTypes;

    /**
     * Gets clazz name.
     *
     * @return the clazz name
     */
    public String getClazzName() {
        return clazzName;
    }

    /**
     * Sets clazz name.
     *
     * @param clazzName the clazz name
     */
    public void setClazzName(String clazzName) {
        this.clazzName = clazzName;
    }

    /**
     * Gets url.
     *
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets url.
     *
     * @param url the url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Gets method name.
     *
     * @return the method name
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Sets method name.
     *
     * @param methodName the method name
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * Get parameter types class [ ].
     *
     * @return the class [ ]
     */
    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    /**
     * Sets parameter types.
     *
     * @param parameterTypes the parameter types
     */
    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

}
