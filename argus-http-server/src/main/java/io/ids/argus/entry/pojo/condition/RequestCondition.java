package io.ids.argus.entry.pojo.condition;

import com.alibaba.fastjson2.JSONObject;
import io.ids.argus.core.json.ArgusJson;
import lombok.Data;

@Data
public class RequestCondition {
    private String path;
    private JSONObject params;
    private JSONObject customized;
}
