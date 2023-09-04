package io.ids.argus.entry.pojo.condition;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

@Data
public class RequestCondition {
    private String path;
    private JSONObject params;
    private JSONObject customized;
}
