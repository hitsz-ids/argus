package io.ids.argus.extension.modelchecking.controller.condition.result;


import io.ids.argus.core.base.enviroment.invoker.IInvokeOutput;
import io.ids.argus.store.grpc.job.JobStoreStatusEnum;
import lombok.Data;

@Data
public class StatusOutput implements IInvokeOutput {
    private JobStoreStatusEnum status;

    public StatusOutput(JobStoreStatusEnum status) {
        this.status = status;
    }
}