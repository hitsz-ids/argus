package io.ids.argus.server.base.service;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import io.ids.argus.core.base.exception.error.InvokerError;
import io.ids.argus.core.base.exception.ArgusInvokerException;
import io.ids.argus.core.base.json.Transformer;
import io.ids.argus.core.conf.log.ArgusLogger;
import io.ids.argus.core.grpc.*;
import io.ids.argus.server.base.module.entity.Request;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class FetchServiceObserver extends BaseObserver {
    public final Request mRequest;
    private final ArgusLogger log = new ArgusLogger(FetchServiceObserver.class);

    public FetchServiceObserver(Request request, StreamObserver<FetchResponse> observer) {
        super(observer);
        this.mRequest = request;
        try {
            var customized = request.getCustomized();
            var params = request.getParams();
            byte[] bytes = FetchData.newBuilder()
                    .setUrl(request.getUrl())
                    .setCustomized(Objects.isNull(customized) ?
                            ByteString.EMPTY.toStringUtf8() :
                            customized.toJsonString())
                    .setParams(Objects.isNull(params) ?
                            ByteString.EMPTY.toStringUtf8() :
                            params.toJsonString())
                    .build().toByteArray();
            send(bytes);
        } catch (Exception e) {
            this.mRequest.exception(e);
            this.mRequest.countDown();
        }
    }

    @Override
    public void onNext(FetchRequest fetchRequest) {
        try {
            mRequest.write(fetchRequest.getData().toByteArray());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new ArgusInvokerException(InvokerError.ERROR_PARSE_RETURN);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        log.error(throwable.getMessage(), throwable);
        mRequest.exception(throwable);
        mRequest.countDown();
        observer.onCompleted();
    }

    @Override
    public void onCompleted() {
        var response = mRequest.getOutputStream().toString(StandardCharsets.UTF_8);
        try {
            mRequest.getOutputStream().close();
            mRequest.setResponse(Transformer.parseObject(response));
            mRequest.countDown();
        } catch (IOException e) {
            mRequest.exception(e);
            mRequest.countDown();
        }
        observer.onCompleted();
    }

}
