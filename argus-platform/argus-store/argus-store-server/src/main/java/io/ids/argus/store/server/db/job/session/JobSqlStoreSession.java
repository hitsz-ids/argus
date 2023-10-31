package io.ids.argus.store.server.db.job.session;

import io.ids.argus.store.grpc.job.JobStoreData;
import io.ids.argus.store.grpc.job.JobStoreStatusEnum;
import io.ids.argus.store.server.db.job.entity.JobEntity;
import io.ids.argus.store.server.db.job.JobMapper;
import io.ids.argus.store.server.db.job.params.CreateParams;
import io.ids.argus.store.server.db.job.params.ListJobParams;
import io.ids.argus.store.server.db.job.params.UpdateStatusParams;
import io.ids.argus.store.server.db.job.result.CreateResult;
import io.ids.argus.store.server.db.job.result.ListJobResult;
import io.ids.argus.store.server.session.ArgusSqlStoreSession;

import java.util.*;

/**
 * Job Store Session is used to operate the database data of Job
 */
public class JobSqlStoreSession extends ArgusSqlStoreSession<JobMapper> {

    @Override
    public Class<JobMapper> getMapper() {
        return JobMapper.class;
    }

    /**
     * Insert Job Data to Database
     *
     * @param params the Job create params
     * @return job create result
     */
    public CreateResult create(CreateParams params) {
        var jobEntity = JobEntity.builder()
                .seq(UUID.randomUUID().toString())
                .module(params.getModule())
                .moduleVersion(params.getModuleVersion())
                .name(params.getName())
                .job(params.getJob())
                .params(params.getParams())
                .createdTime(new Date())
                .status(params.getStatus().getNumber())
                .build();
        mapper.insert(jobEntity);

        return CreateResult.builder()
                .id(jobEntity.getId())
                .seq(jobEntity.getSeq())
                .build();
    }

    /**
     * Update Job status
     *
     * @param params update job status params
     */
    public void updateStatus(UpdateStatusParams params) {
        Date startTime = null;
        Date endTime = null;
        if (Objects.equals(params.getStatus(), JobStoreStatusEnum.EXECUTING)) {
            startTime = new Date();
        } else {
            endTime = new Date();
        }
        mapper.updateStatus(params.getSeq(), params.getStatus().getNumber(), startTime, endTime);
    }

    /**
     * List Jobs By Condition
     *
     * @param params list job params
     * @return jon information
     */
    public ListJobResult listJob(ListJobParams params) {
        var jobStoreDataList = mapper.listJob(params.getModule(), params.getVersion(), params.getStatus());
        var jobList = new ArrayList<JobStoreData>();
        for (ListJobResult.JobData jobData : jobStoreDataList) {
            jobList.add(JobStoreData.newBuilder()
                    .setSeq(jobData.getSeq())
                    .setJob(jobData.getJob())
                    .setParams(jobData.getParams())
                    .build());
        }
        return ListJobResult.builder()
                .list(jobList)
                .build();
    }

    public JobStoreStatusEnum readState(String seq) {
        int status = mapper.selectStatusBySeq(seq);
        return JobStoreStatusEnum.forNumber(status);
    }
}
