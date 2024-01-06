//package io.ids.argus.store.client.session;
//
//import io.grpc.Channel;
//import io.grpc.ManagedChannel;
//import io.ids.argus.store.grpc.SessionType;
//import io.ids.argus.store.grpc.file.CreateFileRequest;
//import io.ids.argus.store.grpc.file.FileStoreServiceGrpc;
//
//public class CreateFileSession extends StoreSession<FileStoreServiceGrpc.FileStoreServiceBlockingStub> {
//    public CreateFileSession(ManagedChannel channel) {
//        super(channel);
//    }
//
//    @Override
//    protected FileStoreServiceGrpc.FileStoreServiceBlockingStub getStub(Channel channel) {
//        return FileStoreServiceGrpc.newBlockingStub(channel);
//    }
//
//    @Override
//    protected SessionType getType() {
//        return SessionType.FILE;
//    }
//
//    public void createFile(CreateFileRequest request){
//        stub.createFile(request);
//    }
//}
