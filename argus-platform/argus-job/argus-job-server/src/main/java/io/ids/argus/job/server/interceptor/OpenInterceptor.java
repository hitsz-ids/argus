package io.ids.argus.job.server.interceptor;

import io.grpc.Context;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.ids.argus.grpc.base.Interceptor;
import io.ids.argus.job.base.ConnectorId;
import io.ids.argus.job.base.JobGrpcContext;
import io.ids.argus.job.grpc.GrpcConstants;
import org.apache.commons.lang3.StringUtils;

public class OpenInterceptor extends Interceptor {
    @Override
    protected Context intercept(Metadata metadata) throws StatusRuntimeException {
        var module = metadata.get(GrpcConstants.MODULE_HEADER);
        var version = metadata.get(GrpcConstants.VERSION_HEADER);
        if (StringUtils.isBlank(module) || StringUtils.isBlank(version)) {
            throw new StatusRuntimeException(Status.NOT_FOUND);
        }
        return JobGrpcContext.addConnectorId(new ConnectorId(module, version));
    }
}
