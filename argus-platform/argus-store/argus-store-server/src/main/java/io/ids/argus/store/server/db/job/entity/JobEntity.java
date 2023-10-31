package io.ids.argus.store.server.db.job.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@TableName("argus_job")
public class JobEntity {

    /**
     * Autoincrement ID
     */
    @TableId(type = IdType.AUTO)
    private Long    id;
    /**
     * Job module
     */
    @TableField("module")
    private String  module;
    /**
     * Job module version
     */
    @TableField("module_version")
    private String  moduleVersion;
    /**
     * Job sequence
     */
    @TableField("seq")
    private String  seq;
    /**
     * Job name
     */
    @TableField("name")
    private String  name;
    /**
     * Job handler name
     */
    @TableField("job")
    private String  job;
    /**
     * Job params
     */
    @TableField("params")
    private String  params;
    /**
     * Job Status
     */
    @TableField("status")
    private Integer status;
    /**
     * Start time of Job
     */
    @TableField("start_time")
    private Date    startTime;
    /**
     * End time of Job
     */
    @TableField("end_time")
    private Date    endTime;
    /**
     * Create time of Job
     */
    @TableField("created_time")
    private Date    createdTime;
}
