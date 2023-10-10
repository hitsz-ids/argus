package io.ids.argus.center.protocol;

import io.ids.argus.center.exception.error.ProtocolError;
import io.ids.argus.center.exception.ArgusProtocolException;
import io.ids.argus.core.base.json.ArgusJson;
import io.ids.argus.core.base.utils.Constants;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;

public class Parser {
    public static Protocol parse(ProtocolData data) {
        var address = data.getPath();
        if (StringUtils.isBlank(address)) {
            throw new ArgusProtocolException(ProtocolError.NULL_ADDRESS);
        }
        Protocol protocol = new Protocol();
        var params = data.getParams();
        if (!ArgusJson.isNull(params)) {
            protocol.setParams(params);
        }
        URI uri;
        try {
            uri = new URI(data.getPath());
        } catch (URISyntaxException e) {
            throw new ArgusProtocolException(ProtocolError.URI_ERROR);
        }
        var path = uri.getPath();
        if (path.startsWith(Constants.URL_SEPARATOR)) {
            path = path.substring(1);
        }
        var index = path.indexOf(Constants.URL_SEPARATOR);
        var module = path.substring(0, index);
        path = path.substring(index + 1);
        if (StringUtils.isEmpty(module)) {
            throw new ArgusProtocolException(ProtocolError.NOT_FOUND_MODULE);
        }
        protocol.setModule(module);

        index = path.indexOf(Constants.URL_SEPARATOR);
        var version = path.substring(0, index);
        if (StringUtils.isEmpty(version)) {
            throw new ArgusProtocolException(ProtocolError.NOT_FOUND_VERSION);
        }
        protocol.setVersion(version);
        path = path.substring(index + 1);
        if (StringUtils.isEmpty(path)) {
            throw new ArgusProtocolException(ProtocolError.NOT_FOUND_URL);
        }
        protocol.setUrl(path);

        return protocol;
    }
}
