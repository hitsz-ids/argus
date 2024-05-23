package io.ids.argus.job.client.process;

import io.ids.argus.core.conf.log.ArgusLogger;
import io.ids.argus.job.client.AArgusJob;
import io.ids.argus.job.client.job.IJobParams;
import io.ids.argus.job.client.job.IJobResult;
import io.ids.argus.store.client.ArgusStore;
import io.ids.argus.store.client.session.UploadSession;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class ProcessJob<P extends IJobParams, R extends IJobResult> extends AArgusJob<P, R> {
    private final ArgusLogger log = new ArgusLogger(ProcessJob.class);
    protected ProcessBuilder pb;
    protected Process pr;
    protected String moduleName;
    protected String fileName;

    protected ProcessJob(String seq, String params) {
        super(seq, params);
        pb = new ProcessBuilder();
    }

    protected String runProcess() throws IOException {
        pr = pb.start();

        SequenceInputStream sis = new SequenceInputStream(pr.getInputStream(), pr.getErrorStream());
        InputStreamReader inst = new InputStreamReader(sis, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(inst);

        String res;
        StringBuilder sb = new StringBuilder();

        while ((res = br.readLine()) != null) {
            sb.append(res).append("\n");
        }
        return sb.toString();
    }

    protected void uploadFile(String fileName, byte[] bytes) throws Exception {
        if (Objects.isNull(moduleName)) {
            throw new Exception("extension name is null");
        }
        try(var session = ArgusStore.get().open(UploadSession.class)){
            try (InputStream stream = new ByteArrayInputStream(bytes)) {
                session.upload(fileName, stream, moduleName, "result", bytes.length);
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    @Override
    public P transform(String params) {
        return null;
    }

    @Override
    public void onStop() {
        log.debug("run job stop");
    }

    @Override
    public void onFailed() {
        log.debug("run job fail");
    }

    @Override
    public R onRun() {
        return null;
    }

    @Override
    public void onComplete() {
        try {
            pr.waitFor();
            pr.destroy();
        } catch (Exception e) {
            log.error("run job error:{}", e.getMessage());
        }
    }
}
