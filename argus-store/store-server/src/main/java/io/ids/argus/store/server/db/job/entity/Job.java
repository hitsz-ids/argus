package io.ids.argus.store.server.db.job.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@TableName("argus_job")
@Data
@Builder
@AllArgsConstructor
public class Job {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("module")
    private String module;
    @TableField("module_version")
    private String moduleVersion;
}
