package io.ids.argus.store.server.db.job.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@TableName("argus_job")
@Data
@Builder
@AllArgsConstructor
public class JobEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("module")
    private String module;
    @TableField("module_version")
    private String moduleVersion;
    @TableField("seq")
    private String seq;
    @TableField("name")
    private String name;
    @TableField("job")
    private String job;
    @TableField("params")
    private String params;
    @TableField("status")
    private Integer status;
    @TableField("start_time")
    private Date startTime;
    @TableField("end_time")
    private Date endTime;
    @TableField("created_time")
    private Date createdTime;
}
