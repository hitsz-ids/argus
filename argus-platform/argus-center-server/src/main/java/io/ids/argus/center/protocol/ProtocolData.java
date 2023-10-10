package io.ids.argus.center.protocol;

import io.ids.argus.core.base.json.ArgusJson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProtocolData {
    private String path;
    private ArgusJson params;
    private ArgusJson customized;
}
