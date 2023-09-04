package io.ids.argus.center.startup;

import io.ids.argus.core.base.common.Namespace;
import io.ids.argus.core.base.json.ArgusJson;
import lombok.Data;

@Data
public class Protocol {
    private String module;
    private String version;
    private Namespace namespace;
    private String url;
    private ArgusJson params;
    private ArgusJson customized;

    private static final String SPECIAL_URL_FOR_QUERY = "/job/'query'/";
}
