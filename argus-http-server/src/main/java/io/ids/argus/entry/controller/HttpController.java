package io.ids.argus.entry.controller;

import io.ids.argus.center.protocol.ProtocolData;
import io.ids.argus.center.startup.Argus;
import io.ids.argus.core.base.json.Transformer;
import io.ids.argus.entry.pojo.condition.RequestCondition;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HttpController {
    @PostMapping("/argus/execute")
    public String api(@RequestBody RequestCondition requestCondition) throws Exception {
        var request = Argus.get().send(ProtocolData.builder()
                        .params(Transformer.parseObject(requestCondition.getParams()))
                        .customized(Transformer.parseObject(requestCondition.getCustomized()))
                        .path(requestCondition.getPath())
                .build());
        if (request.isSuccess()) {
            return request.getResponse().toJsonString();
        } else {
            throw request.getException();
        }
    }

}
