package io.ids.argus.server.base.module.configuration;

import java.util.Objects;

public class ModuleEntity {
    private final String name;
    private final boolean enable;
    private final String[] version;

    public String[] getVersion() {
        return version;
    }

    public ModuleEntity(String name, String[] version) {
        this.name = name;
        this.version = version;
        enable = true;
    }

    public boolean support(String version) {
        for (String s : this.version) {
            if(Objects.equals(s, version)) {
                return true;
            }
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public boolean isEnable() {
        return enable;
    }
}
