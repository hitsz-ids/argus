package io.ids.argus.entry.controller;

import io.ids.argus.center.protocol.ProtocolData;
import io.ids.argus.center.startup.Argus;
import io.ids.argus.core.base.json.Transformer;
import io.ids.argus.entry.pojo.condition.DownloadCondition;
import io.ids.argus.entry.pojo.condition.RequestCondition;
import io.ids.argus.entry.pojo.condition.UploadCondition;
import io.ids.argus.store.client.ArgusStore;
import io.ids.argus.store.client.session.DownloadSession;
import io.ids.argus.store.client.session.UploadSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

@RestController
public class HttpController {

    @PostMapping("/argus/execute")
    public String api(@RequestBody RequestCondition requestCondition,
                      HttpServletRequest servletRequest,
                      HttpServletResponse servletResponse) throws Exception {
        var request = Argus.get().send(ProtocolData.builder()
                .params(Transformer.parseObject(requestCondition.getParams()))
                .customized(Transformer.parseObject(requestCondition.getCustomized()))
                .path(requestCondition.getPath())
                .build());
        servletRequest.setCharacterEncoding("UTF-8");
        servletResponse.setCharacterEncoding("UTF-8");
        servletResponse.setContentType("application/json;charset=UTF-8");
        if (request.isSuccess()) {
            return request.getResponse().toJsonString();
        } else {
            throw request.getException();
        }
    }

    @PutMapping("/argus/upload")
    public void upload(UploadCondition condition) throws Exception {
        ArgusStore.init();
        try (var session = ArgusStore.get().open(UploadSession.class)) {
            session.upload(condition.getFileName(), condition.getFile(), condition.getModule(), condition.getDirectory());
        }
    }

    @GetMapping("/argus/download")
    public void download(DownloadCondition condition, HttpServletResponse servletResponse) throws Exception {
        ArgusStore.init();
        try (var session = ArgusStore.get().open(DownloadSession.class)) {
            session.download(servletResponse, condition.getFileName(), condition.getModule(), condition.getDirectory());
        }
    }
}
