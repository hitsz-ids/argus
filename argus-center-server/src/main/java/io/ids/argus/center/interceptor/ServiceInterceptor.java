package io.ids.argus.center.interceptor;

import io.grpc.*;
import io.ids.argus.center.common.NetworkStatus;
import io.ids.argus.center.common.GrpcContext;
import io.ids.argus.center.exception.ArgusInterceptException;
import io.ids.argus.center.module.ModuleManager;
import io.ids.argus.center.module.LockPool;
import io.ids.argus.core.base.conf.ArgusLogger;
import io.ids.argus.core.base.utils.Constant;
import io.ids.argus.core.transport.grpc.ArgusModule;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class ServiceInterceptor extends Interceptor {
    private final ArgusLogger log = new ArgusLogger(ServiceInterceptor.class);

    @Override
    protected Context intercept(Metadata metadata) throws ArgusInterceptException {
        String secret = getHeader(Constant.HEADER_MODULE_NAME, metadata);
        if (StringUtils.isEmpty(secret)) {
            throw new ArgusInterceptException(NetworkStatus.UNAUTHENTICATED);
        }
        String version = getHeader(Constant.HEADER_MODULE_VERSION, metadata);
        if (StringUtils.isEmpty(version)) {
            throw new ArgusInterceptException(NetworkStatus.UNAUTHENTICATED);
        }
        ArgusModule module = ModuleManager.get().validate(secret, version);
        if (Objects.isNull(module)) {
            log.error("没有找到对应模块，请检查模块是否已经注册到中心");
            throw new ArgusInterceptException(NetworkStatus.NOT_FOUND);
        }
        LockPool.get().lock(module);
        try {
            log.debug("模块【{}:{}】登录中",
                    module.getName(),
                    module.getVersion());
            if (ModuleManager.get().isLogin(module)) {
                log.error("模块已经成功登录，请不要重复注册");
                throw new ArgusInterceptException(NetworkStatus.ALREADY_EXISTS);
            }
            return GrpcContext.addModule(Context.current(), module);
        } finally {
            LockPool.get().unlock(module);
        }
    }
}
