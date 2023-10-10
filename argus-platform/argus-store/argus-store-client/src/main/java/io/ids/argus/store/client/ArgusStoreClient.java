package io.ids.argus.store.client;

import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.ids.argus.grpc.base.ArgusGrpcClient;
import io.ids.argus.store.base.StoreAddress;
import io.ids.argus.store.client.exception.error.StoreError;
import io.ids.argus.store.client.exception.ArgusStoreException;
import io.ids.argus.store.client.session.StoreSession;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ArgusStoreClient extends ArgusGrpcClient {
    ArgusStoreClient(StoreAddress address) {
        super(address);
    }

    public <S extends StoreSession<?>> S openJobSession(Class<S> sClass) {
        Constructor<S> constructor;
        try {
            constructor = sClass.getConstructor(ManagedChannel.class);
            return constructor.newInstance(getChannel());
        } catch (NoSuchMethodException |
                 InstantiationException |
                 IllegalAccessException |
                 InvocationTargetException e) {
            throw new ArgusStoreException(StoreError.ERROR_CREATE);
        }
    }

    @Override
    protected Metadata createHeader() {
        return new Metadata();
    }
}
