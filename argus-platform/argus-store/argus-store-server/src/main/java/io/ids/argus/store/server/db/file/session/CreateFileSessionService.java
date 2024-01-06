//package io.ids.argus.store.server.db.file.session;
//
//import io.grpc.stub.StreamObserver;
//import io.ids.argus.store.grpc.file.CreateFileRequest;
//import io.ids.argus.store.grpc.file.CreateFileResponse;
//import io.ids.argus.store.server.db.file.params.CreateFileParams;
//import io.ids.argus.store.server.service.IService;
//
///**
// * Session GRPC service
// */
//public class CreateFileSessionService implements IService<FileStoreSession> {
//
//    @Override
//    public void createFile(CreateFileRequest request, StreamObserver<CreateFileResponse> responseObserver) {
//        var session = getSqlSession();
//        var result = session.createFile(CreateFileParams.builder()
//                .module(request.getModuleName())
//                .moduleVersion(request.getModuleVersion())
//                .directory(request.getDirectoryName())
//                .fileName(request.getFileName())
//                .build());
//        responseObserver.onNext(CreateFileResponse.newBuilder()
//                .setId(result.getId())
//                .setFileId(result.getFileId())
//                .build());
//        responseObserver.onCompleted();
//    }
//}
