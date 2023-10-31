package io.ids.argus.store.server.db.job;

import io.ids.argus.store.server.db.job.entity.JobEntity;
import io.ids.argus.store.server.db.job.result.ListJobResult;
import io.ids.argus.store.server.db.mapper.ArgusMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface JobMapper extends ArgusMapper<JobEntity> {

    /**
     * Update Job status
     *
     * @param seq Job sequence
     * @param status Job status
     * @param startTime Job start time
     * @param endTime Job end time
     */
    void updateStatus(@Param("seq") String seq,
                      @Param("status") Integer status,
                      @Param("startTime") Date startTime,
                      @Param("endTime") Date endTime);

    /**
     * List Job Info
     *
     * @param module
     * @param version
     * @param status
     * @return
     */
    List<ListJobResult.JobData> listJob(@Param("module") String module,
                                        @Param("version") String version,
                                        @Param("status") Integer status);

    Integer selectStatusBySeq(@Param("seq") String seq);

}
