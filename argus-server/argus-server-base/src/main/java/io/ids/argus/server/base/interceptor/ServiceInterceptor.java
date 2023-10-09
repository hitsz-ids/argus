package io.ids.argus.server.base.interceptor;

import io.grpc.Context;
import io.grpc.Metadata;
import io.ids.argus.core.conf.log.ArgusLogger;
import io.ids.argus.server.base.exception.error.NetworkError;
import io.ids.argus.server.base.exception.ArgusInterceptException;
import io.ids.argus.server.base.module.configuration.ModuleConfiguration;
import io.ids.argus.server.base.module.manager.ArgusModuleManager;
import io.ids.argus.server.base.service.LockPool;
import io.ids.argus.server.base.utils.GrpcContext;
import io.ids.argus.server.grpc.GrpcConstants;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class ServiceInterceptor extends ServerBaseInterceptor {
    private final ArgusLogger log = new ArgusLogger(ServiceInterceptor.class);

    public ServiceInterceptor(ModuleConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected Context baseIntercept(Metadata metadata) throws ArgusInterceptException {
        var secret = metadata.get(GrpcConstants.HEADER_MODULE_NAME_SECRET);
        if (StringUtils.isEmpty(secret)) {
            throw new ArgusInterceptException(NetworkError.UNAUTHENTICATED);
        }
        var version = metadata.get(GrpcConstants.HEADER_MODULE_VERSION);
        if (StringUtils.isEmpty(version)) {
            throw new ArgusInterceptException(NetworkError.UNAUTHENTICATED);
        }
        var module = configuration.validate(secret, version);
        if (Objects.isNull(module)) {
            log.error("没有找到对应模块，请检查模块是否已经注册到中心");
            throw new ArgusInterceptException(NetworkError.NOT_FOUND);
        }
        LockPool.get().lock(module);
        try {
            log.debug("模块【{}:{}】登录中",
                    module.getName(),
                    module.getVersion());
            if (ArgusModuleManager.get().isLogin(module)) {
                log.error("模块已经成功登录，请不要重复注册");
                throw new ArgusInterceptException(NetworkError.ALREADY_EXISTS);
            }
            return GrpcContext.addModule(Context.current(), module);
        } finally {
            LockPool.get().unlock(module);
        }
    }
}
