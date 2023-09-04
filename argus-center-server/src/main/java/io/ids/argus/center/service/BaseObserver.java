package io.ids.argus.center.service;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import io.ids.argus.core.base.utils.Constant;
import io.ids.argus.core.transport.grpc.FetchRequest;
import io.ids.argus.core.transport.grpc.FetchResponse;

public abstract class BaseObserver implements StreamObserver<FetchRequest> {
    protected final StreamObserver<FetchResponse> observer;

    public BaseObserver(StreamObserver<FetchResponse> observer) {
        this.observer = observer;
    }

    protected void send(byte[] bytes) {
        var total = bytes.length;
        var sendLength = Math.min(Constant.SEND_STREAM_MAX_SIZE, total);
        var start = 0;
        while (total > 0) {
            sendLength = Math.min(Constant.SEND_STREAM_MAX_SIZE, total);
            var dest = new byte[sendLength];
            System.arraycopy(bytes, start, dest, 0, sendLength);
            observer.onNext(FetchResponse.newBuilder()
                    .setBytes(ByteString.copyFrom(dest))
                    .build());
            start = sendLength;
            total = total - sendLength;
        }
        observer.onNext(FetchResponse.newBuilder().setEof(true).build());
    }
}
