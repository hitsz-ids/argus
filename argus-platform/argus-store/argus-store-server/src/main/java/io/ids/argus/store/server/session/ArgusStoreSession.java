package io.ids.argus.store.server.session;

public abstract class ArgusStoreSession implements AutoCloseable {

    public abstract void commit();

    public abstract void rollback();

    @Override
    public abstract void close();
}
