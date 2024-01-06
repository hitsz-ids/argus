package io.ids.argus.extension.modelchecking.controller;

import io.ids.argus.core.base.module.annotation.API;
import io.ids.argus.core.base.module.annotation.ArgusController;
import io.ids.argus.core.base.module.controller.IArgusController;
import io.ids.argus.extension.modelchecking.controller.condition.args.ModelCheckingInfoStatusArgs;
import io.ids.argus.extension.modelchecking.controller.condition.args.ModelCheckingValidateArgs;
import io.ids.argus.extension.modelchecking.controller.condition.entity.ModelCheckingJobEntity;
import io.ids.argus.extension.modelchecking.controller.condition.result.StatusOutput;
import io.ids.argus.extension.modelchecking.job.ModelCheckingJobParams;
import io.ids.argus.store.client.ArgusStore;
import io.ids.argus.store.client.session.JobSession;
import io.ids.argus.store.grpc.job.JobStoreStatusEnum;

import java.util.Objects;

@ArgusController
public class ModelCheckingController implements IArgusController {
    @API(url = "/model-checking/validate")
    public ModelCheckingJobEntity validate(ModelCheckingValidateArgs args) {
        var params = new ModelCheckingJobParams();
        params.setPath(args.getPath());
        return new ModelCheckingJobEntity(params);
    }

    @API(url = "/model-checking/status")
    public StatusOutput getStatus(ModelCheckingInfoStatusArgs args) throws Exception {
        ArgusStore.init();
        try (var session = ArgusStore.get().open(JobSession.class)) {
            JobStoreStatusEnum status = session.readState(args.getSeq());
            if (Objects.isNull(status)) {
                return new StatusOutput(JobStoreStatusEnum.UNKNOWN);
            }
            return new StatusOutput(status);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
