package io.ids.argus.extension.modelchecking.controller;
import io.ids.argus.core.base.module.annotation.API;
import io.ids.argus.core.base.module.annotation.ArgusController;
import io.ids.argus.core.base.module.controller.IArgusController;
import io.ids.argus.extension.modelchecking.controller.condition.args.ModelScanValidateArgs;
import io.ids.argus.extension.modelchecking.controller.condition.entity.ModelScanJobEntity;
import io.ids.argus.extension.modelchecking.job.ModelScanJobParams;
import org.apache.commons.codec.binary.Base64;

@ArgusController
public class ModelCheckingController implements IArgusController {
    @API(url = "/model-checking/validate")
    public ModelScanJobEntity validate(ModelScanValidateArgs args) {
        var params = new ModelScanJobParams();
        params.setPath(Base64.encodeBase64String(args.getPath().getBytes()));
        return new ModelScanJobEntity(params);
    }
}
