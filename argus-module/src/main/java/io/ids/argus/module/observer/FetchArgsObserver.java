package io.ids.argus.module.observer;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.ids.argus.core.base.common.InvokerStatus;
import io.ids.argus.core.base.enviroment.invoker.Invoker;
import io.ids.argus.core.base.exception.ArgusInvokerException;
import io.ids.argus.core.base.json.Transformer;
import io.ids.argus.core.base.utils.Constant;
import io.ids.argus.core.transport.grpc.FetchData;
import io.ids.argus.core.transport.grpc.FetchRequest;
import io.ids.argus.core.transport.grpc.FetchResponse;
import io.ids.argus.module.context.ModuleContext;

import java.nio.charset.StandardCharsets;

public class FetchArgsObserver implements StreamObserver<FetchResponse> {
    private ByteString stream = ByteString.EMPTY;
    private StreamObserver<FetchRequest> server;
    private final ModuleContext context;

    public FetchArgsObserver(ModuleContext context) {
        this.context = context;
    }

    public void setServer(StreamObserver<FetchRequest> server) {
        this.server = server;
    }

    @Override
    public void onNext(FetchResponse fetchResponse) {
        try {
            if (fetchResponse.getEof()) {
                try {
                    var fetchData = FetchData.parseFrom(stream.toByteArray());
                    if (!context.contains(fetchData.getUrl(), fetchData.getNamespace())) {
                        throw new ArgusInvokerException(InvokerStatus.NOT_FOUND_URL);
                    }
                    var data = new Invoker.Data(
                            fetchData.getNamespace(),
                            fetchData.getUrl(),
                            Transformer.parseObject(fetchData.getParams()),
                            Transformer.parseObject(fetchData.getCustomized()));
                    var output = context.invoke(data);
                    callback(output.getBytes(StandardCharsets.UTF_8));
                } catch (InvalidProtocolBufferException e) {
                    throw new ArgusInvokerException(InvokerStatus.ERROR_PARSE_INVOKE_DATA);
                }
            } else {
                stream = stream.concat(fetchResponse.getBytes());
            }
        } catch (Exception e) {
            server.onError(Status.INTERNAL.withDescription(e.getMessage()).asException());
        }
    }

    public void callback(byte[] bytes) {
        var total = bytes.length;
        var sendLength = Math.min(Constant.SEND_STREAM_MAX_SIZE, total);
        var start = 0;
        while (total > 0) {
            sendLength = Math.min(Constant.SEND_STREAM_MAX_SIZE, total);
            var dest = new byte[sendLength];
            System.arraycopy(bytes, start, dest, 0, sendLength);
            server.onNext(FetchRequest.newBuilder()
                    .setBytes(ByteString.copyFrom(dest))
                    .build());
            start = sendLength;
            total = total - sendLength;
        }
        server.onCompleted();
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onCompleted() {

    }
}
