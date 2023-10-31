package io.ids.argus.center.protocol;

import io.ids.argus.core.base.json.ArgusJson;
import lombok.Data;

/**
 * The Protocol to access Argus Module APIs
 *
 */
@Data
public class Protocol {

    private String module;
    private String version;
    private String url;
    private ArgusJson params;
    private ArgusJson customized;

}
