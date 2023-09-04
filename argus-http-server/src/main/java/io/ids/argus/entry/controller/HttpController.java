package io.ids.argus.entry.controller;

import io.ids.argus.center.startup.Argus;
import io.ids.argus.center.startup.RequestData;
import io.ids.argus.core.base.json.Transformer;
import io.ids.argus.entry.pojo.condition.RequestCondition;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;

@RestController
public class HttpController {
    @PostMapping("/argus/entry")
    public String api(@RequestBody RequestCondition request) throws URISyntaxException {
        var json = Argus.get().send(RequestData.builder()
                        .params(Transformer.parseObject(request.getParams()))
                        .customized(Transformer.parseObject(request.getCustomized()))
                        .path(request.getPath())
                .build());

        return json.toJsonString();
    }
}
