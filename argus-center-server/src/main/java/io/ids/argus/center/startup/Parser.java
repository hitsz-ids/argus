package io.ids.argus.center.startup;

import io.ids.argus.center.common.ParserStatus;
import io.ids.argus.center.exception.ArgusProtocolException;
import io.ids.argus.core.base.common.Namespace;
import io.ids.argus.core.base.json.ArgusJson;
import io.ids.argus.core.base.utils.Constant;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class Parser {
    public static Protocol parse(RequestData requestData) throws URISyntaxException {
        var address = requestData.getPath();
        if (StringUtils.isBlank(address)) {
            throw new ArgusProtocolException(ParserStatus.NULL_ADDRESS);
        }
        Protocol protocol = new Protocol();
        var params = requestData.getParams();
        if (!ArgusJson.isNull(params)) {
            protocol.setParams(params);
        }
        var uri = new URI(requestData.getPath());
        var path = uri.getPath();
        if (path.startsWith(Constant.URL_SEPARATOR)) {
            path = path.substring(1);
        }
        var index = path.indexOf(Constant.URL_SEPARATOR);
        var module = path.substring(0, index);
        path = path.substring(index + 1);
        if (StringUtils.isEmpty(module)) {
            throw new ArgusProtocolException(ParserStatus.NOT_FOUND_MODULE);
        }
        protocol.setModule(module);

        index = path.indexOf(Constant.URL_SEPARATOR);
        var version = path.substring(0, index);
        if (StringUtils.isEmpty(version)) {
            throw new ArgusProtocolException(ParserStatus.NOT_FOUND_VERSION);
        }
        protocol.setVersion(version);

        path = path.substring(index + 1);
        index = path.indexOf(Constant.URL_SEPARATOR);
        if (index == -1) {
            throw new ArgusProtocolException(ParserStatus.NOT_FOUND_NAMESPACE);
        }
        var namespace = Namespace.get(path.substring(0, index));
        if (Objects.isNull(namespace)) {
            throw new ArgusProtocolException(ParserStatus.NOT_FOUND_NAMESPACE);
        }
        if (namespace == Namespace.JOB) {
            var customized = requestData.getCustomized();
            if (!ArgusJson.isNull(customized)) {
                protocol.setCustomized(customized);
            }
        }
        protocol.setNamespace(namespace);

        var url = path.substring(index + 1);
        if (StringUtils.isEmpty(url)) {
            throw new ArgusProtocolException(ParserStatus.NOT_FOUND_URL);
        }
        protocol.setUrl(url);

        return protocol;
    }
}
