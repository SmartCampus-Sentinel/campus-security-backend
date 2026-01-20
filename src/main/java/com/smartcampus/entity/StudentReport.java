package com.smartcampus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 学生隐患上报表
 * </p>
 *
 * @author SmartCampusTeam
 * @since 2026-01-20
 */
@Getter
@Setter
@TableName("student_report")
@ApiModel(value = "StudentReport对象", description = "学生隐患上报表")
public class StudentReport implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("上报ID")
    @TableId(value = "report_id", type = IdType.AUTO)
    private Long reportId;

    @ApiModelProperty("学生ID(或OpenID)")
    @TableField("student_id")
    private String studentId;

    @ApiModelProperty("隐患类型")
    @TableField("report_type")
    private String reportType;

    @ApiModelProperty("位置描述")
    @TableField("location")
    private String location;

    @ApiModelProperty("详细描述")
    @TableField("description")
    private String description;

    @ApiModelProperty("多媒体文件URL(逗号分隔)")
    @TableField("media_url")
    private String mediaUrl;

    @ApiModelProperty("上报时间")
    @TableField("report_time")
    private LocalDateTime reportTime;

    @ApiModelProperty("审核状态(0:待审核 1:已处理 2:驳回)")
    @TableField("audit_status")
    private Byte auditStatus;
}
