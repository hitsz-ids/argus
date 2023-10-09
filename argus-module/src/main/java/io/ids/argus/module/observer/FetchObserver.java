package io.ids.argus.module.observer;

import com.google.protobuf.ByteString;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.ids.argus.core.base.exception.error.InvokerError;
import io.ids.argus.core.base.enviroment.invoker.Invoker;
import io.ids.argus.core.base.exception.ArgusInvokerException;
import io.ids.argus.core.base.json.Transformer;
import io.ids.argus.core.base.utils.Utils;
import io.ids.argus.core.conf.log.ArgusLogger;
import io.ids.argus.core.grpc.*;
import io.ids.argus.job.client.ArgusJob;
import io.ids.argus.module.context.ModuleContext;
import io.ids.argus.module.entity.StopJobRequest;
import io.ids.argus.module.entity.StopJobResponse;
import io.ids.argus.server.grpc.GrpcConstants;

import java.nio.charset.StandardCharsets;

public class FetchObserver implements StreamObserver<FetchResponse> {
    private final ArgusLogger log = new ArgusLogger(FetchObserver.class);
    private ByteString stream = ByteString.EMPTY;
    private StreamObserver<FetchRequest> server;
    private final ModuleContext context;

    public FetchObserver(ModuleContext context) {
        this.context = context;
    }

    public void setServer(StreamObserver<FetchRequest> server) {
        this.server = server;
    }

    @Override
    public void onNext(FetchResponse fetchResponse) {
        try {
            if (fetchResponse.getEof()) {
                var fetchData = FetchData.parseFrom(stream.toByteArray());
                var url = Utils.pack(fetchData.getUrl());
                if (context.special(url)) {
                    var request = Transformer.parseObject(fetchData.getParams(), StopJobRequest.class);
                    ArgusJob.get().stop(request.getSeq());
                    callback(StopJobResponse.transmission());
                    return;
                }
                if (!context.contains(url)) {
                    throw new ArgusInvokerException(InvokerError.NOT_FOUND_URL);
                }
                var data = new Invoker.Data(
                        url,
                        Transformer.parseObject(fetchData.getParams()),
                        Transformer.parseObject(fetchData.getCustomized()));
                var output = context.invoke(data);
                callback(output.getBytes(StandardCharsets.UTF_8));
            } else {
                stream = stream.concat(fetchResponse.getBytes());
            }
        } catch (Exception e) {
            server.onError(Status.INTERNAL.withDescription(e.getMessage()).asException());
        }
    }

    public void callback(byte[] bytes) {
        var total = bytes.length;
        var sendLength = Math.min(GrpcConstants.SEND_STREAM_MAX_SIZE, total);
        var start = 0;
        while (total > 0) {
            sendLength = Math.min(GrpcConstants.SEND_STREAM_MAX_SIZE, total);
            var dest = new byte[sendLength];
            System.arraycopy(bytes, start, dest, 0, sendLength);
            server.onNext(FetchRequest.newBuilder()
                    .setData(ByteString.copyFrom(dest))
                    .build());
            start = sendLength;
            total = total - sendLength;
        }
        server.onCompleted();
    }

    @Override
    public void onError(Throwable throwable) {
        log.error(throwable.getMessage(), throwable);
    }

    @Override
    public void onCompleted() {
    }
}
