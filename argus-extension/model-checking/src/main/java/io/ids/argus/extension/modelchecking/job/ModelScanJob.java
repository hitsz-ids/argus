package io.ids.argus.extension.modelchecking.job;

import io.ids.argus.core.base.json.Transformer;
import io.ids.argus.core.conf.log.ArgusLogger;
import io.ids.argus.extension.modelchecking.conf.ModelCheckingProperties;
import io.ids.argus.job.client.AArgusJob;
import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class ModelScanJob extends AArgusJob<ModelScanJobParams, ModelScanJobResult> {
    private final ArgusLogger log = new ArgusLogger(ModelScanJob.class);

    public ModelScanJob(String seq, String params) {
        super(seq, params);
    }

    @Override
    public ModelScanJobParams transform(String params) {
        return Transformer.fromJson(params, ModelScanJobParams.class);
    }

    @Override
    public void onStop() {
        log.debug("scan stop");
    }

    @Override
    public void onFailed() {
        log.debug("scan fail");
    }

    @Override
    public ModelScanJobResult onRun() {
        String result = null;
        if (running()) {
            result = this.scan();
        }
        return new ModelScanJobResult(result);
    }

    private String scan() {
        try {
            var scanResultPath = Paths.get(ModelCheckingProperties.get().getScanResultPath());
            var scanResultFile = this.getModelFilePath();
            // execute node script
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec("node " + this.getScanScriptCommand());

            SequenceInputStream sis = new SequenceInputStream(pr.getInputStream(), pr.getErrorStream());
            InputStreamReader inst = new InputStreamReader(sis, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(inst);

            String res;
            StringBuilder sb = new StringBuilder();
            // save scan result
            while ((res = br.readLine()) != null) {
                sb.append(res).append("\n");
            }
            pr.waitFor();
            pr.destroy();
            log.debug("scan result:" + sb);
            // save result file
            File file = new File(scanResultPath + File.separator + scanResultFile);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(sb.toString().getBytes());
            fos.close();
            return sb.toString();
        } catch (Exception e) {
            log.error("scan error:{}", e.getMessage());
        }
        return null;
    }

    private String getScanScriptCommand() {
        var modelCheckPath = Paths.get(ModelCheckingProperties.get().getModelCheckPath());
        var modelFilesPath = Paths.get(ModelCheckingProperties.get().getModelFilesPath());
        return modelCheckPath + File.separator + "serve.js "
                + modelFilesPath + File.separator + new String(Base64.decodeBase64(this.getModelFilePath()));
    }

    private String getModelFilePath() {
        ModelScanJobParams params = this.getParams();
        return params.getPath();
    }

    @Override
    public void onComplete() {
        log.debug("scan task complete");
    }
}
