package io.ids.argus.server.base.module.entity;

import io.ids.argus.core.base.json.ArgusJson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestBody {
    private String url;
    private ArgusJson params;
    private ArgusJson customized;
}
