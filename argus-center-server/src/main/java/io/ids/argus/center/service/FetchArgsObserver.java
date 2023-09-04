package io.ids.argus.center.service;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import io.ids.argus.core.base.conf.ArgusLogger;
import io.ids.argus.core.transport.grpc.FetchData;
import io.ids.argus.core.transport.grpc.FetchRequest;
import io.ids.argus.core.transport.grpc.FetchResponse;

import java.util.Objects;

public class FetchArgsObserver extends BaseObserver {
    public final RequestManager.Request mRequest;
    private final ArgusLogger log = new ArgusLogger(FetchArgsObserver.class);

    public FetchArgsObserver(RequestManager.Request request, StreamObserver<FetchResponse> observer) {
        super(observer);
        this.mRequest = request;
        var command = request.getCommand();
        var customized = command.getCustomized();
        var params = command.getParams();
        byte[] bytes = FetchData.newBuilder()
                .setUrl(command.getUrl())
                .setNamespace(command.getNamespace().getName())
                .setCustomized(Objects.isNull(customized) ?
                        ByteString.EMPTY.toStringUtf8() :
                        customized.toJsonString())
                .setParams(Objects.isNull(params) ?
                        ByteString.EMPTY.toStringUtf8() :
                        params.toJsonString())
                .build().toByteArray();
        send(bytes);
    }

    @Override
    public void onNext(FetchRequest fetchRequest) {
        mRequest.append(fetchRequest.getBytes().toStringUtf8());
    }

    @Override
    public void onError(Throwable throwable) {
        log.error(throwable.getMessage(), throwable);
        mRequest.countDown();
        observer.onCompleted();
    }

    @Override
    public void onCompleted() {
        mRequest.countDown();
    }
}
