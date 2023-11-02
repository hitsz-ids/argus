package io.ids.argus.grpc.base;

import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.AbstractBlockingStub;
import io.grpc.stub.AbstractStub;
import io.grpc.stub.MetadataUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * The Argus GRPC client
 */
public abstract class ArgusGrpcClient {

    private ManagedChannel channel;
    private final ArgusAddress address;

    protected ArgusGrpcClient(ArgusAddress address) {
        this.address = address;
        initChannel();
    }

    private void initChannel() {
        if (Objects.isNull(channel) || channel.isTerminated() || channel.isShutdown()) {
            channel = NettyChannelBuilder.forAddress(address.getAddress())
                    .usePlaintext()
                    .keepAliveWithoutCalls(true)
                    .keepAliveTime(10, TimeUnit.SECONDS)
                    .keepAliveTimeout(5, TimeUnit.SECONDS)
                    .build();
        }
    }

    public ManagedChannel getChannel() {
        initChannel();
        return channel;
    }

    public <S extends AbstractBlockingStub<S>> S getBlockingStub(Class<?> clazz,
                                                                 Class<S> sClass) {
        try {
            var method = clazz.getMethod("newBlockingStub", io.grpc.Channel.class);
            if (Objects.equals(method.getReturnType(), sClass)) {
                var s = (S) method.invoke(null, getChannel());
                s = s.withDeadlineAfter(60, TimeUnit.SECONDS)
                        .withInterceptors(MetadataUtils.newAttachHeadersInterceptor(createHeader()));
                return s;
            }
        } catch (IllegalAccessException |
                 NoSuchMethodException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public <S extends AbstractStub<S>> S getStub(Class<?> clazz,
                                                 Class<S> sClass) {
        try {
            var method = clazz.getMethod("newStub", io.grpc.Channel.class);
            if (Objects.equals(method.getReturnType(), sClass)) {
                var s = (S) method.invoke(getChannel());
                s = s.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(createHeader()));
                return s;
            }
        } catch (IllegalAccessException |
                 NoSuchMethodException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    protected abstract Metadata createHeader();
}
