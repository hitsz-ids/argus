package io.ids.argus.store.server.db.file.entity;

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
@TableName("argus_file")
public class FileEntity {

    /**
     * Autoincrement ID
     */
    @TableId(type = IdType.AUTO)
    private Long    id;
    /**
     *  module
     */
    @TableField("module")
    private String  module;
    /**
     *  module version
     */
    @TableField("module_version")
    private String  moduleVersion;
    /**
     *  file path
     */
    @TableField("file_path")
    private String filePath;
    /**
     * Job sequence
     */
    @TableField("file_id")
    private String  fileId;
    /**
     * file name
     */
    @TableField("file_name")
    private String  fileName;
    /**
     * file Status
     */
    @TableField("status")
    private Integer status;
    /**
     * is_deleted
     */
    @TableField("is_deleted")
    private Integer isDeleted;
    /**
     * Create time of file
     */
    @TableField("created_time")
    private Date    createdTime;
}
