package io.ids.argus.extension.modelchecking.job;

import io.ids.argus.core.base.json.Transformer;
import io.ids.argus.core.conf.log.ArgusLogger;
import io.ids.argus.job.client.process.ProcessJob;
import io.ids.argus.module.conf.ModuleProperties;

import java.io.File;
import java.nio.file.Paths;

public class ModelCheckingJob extends ProcessJob<ModelCheckingJobParams, ModelCheckingJobResult> {
    private final ArgusLogger log = new ArgusLogger(ModelCheckingJob.class);

    public ModelCheckingJob(String seq, String params) {
        super(seq, params);
        moduleName = ModuleProperties.get().getName();
        fileName = this.getParams().getFileName();
    }

    @Override
    public ModelCheckingJobParams transform(String params) {
        return Transformer.fromJson(params, ModelCheckingJobParams.class);
    }

    @Override
    public ModelCheckingJobResult onRun() {
        String result = null;
        if (running()) {
            result = this.modelChecking();
        }
        return new ModelCheckingJobResult(result);
    }

    private String modelChecking() {
        try {
            pb.command("node", this.getScriptCommand(), this.getModelFilePath());
            String res = runProcess();
            // 保存结果文件 格式为 .txt
            uploadFile(fileName + ".txt", res.getBytes());
            return null;
        } catch (Exception e) {
            log.error("scan error:{}", e.getMessage());
        }
        return null;
    }

    private String getScriptCommand() {
        return Paths.get("argus-extension/modelchecking-server/dist").toAbsolutePath() + File.separator + "serve.js";
    }
    private String getModelFilePath() {
        return Paths.get("storage/modelchecking/model").toAbsolutePath() + File.separator + fileName;
    }

    @Override
    public void onComplete() {
        super.onComplete();
        log.info("model-checking task complete");
    }
}
