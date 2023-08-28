package io.ids.argus.core.common;

public enum Namespace {
    API(0, "api"),
    JOB(1, "job"),
    ;
    private final int type;
    private final String name;

    Namespace(int type, String name) {
        this.type = type;
        this.name = name;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * Gets namespace.
     *
     * @return the namespace
     */
    public String getName() {
        return name;
    }

    /**
     * 返回当前namespace
     *
     * @param name 对应的namespace的名称
     * @return 对应的namespace
     */
    public static Namespace get(String name) {
        for (Namespace item : Namespace.values()) {
            if (item.name.equals(name)) {
                return item;
            }
        }
        return null;
    }
}
