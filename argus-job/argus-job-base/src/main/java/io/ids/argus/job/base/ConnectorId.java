package io.ids.argus.job.base;

import java.util.Objects;

public record ConnectorId(String module, String version) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectorId that = (ConnectorId) o;
        return Objects.equals(module, that.module) && Objects.equals(version, that.version);
    }

}
