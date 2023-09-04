package io.ids.argus.center.startup;

import io.ids.argus.core.base.json.ArgusJson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestData {
    private String path;
    private ArgusJson params;
    private ArgusJson customized;
}
