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
 * 报警事件记录表
 * </p>
 *
 * @author SmartCampusTeam
 * @since 2026-01-20
 */
@Getter
@Setter
@TableName("alarm_event")
@ApiModel(value = "AlarmEvent对象", description = "报警事件记录表")
public class AlarmEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("报警ID")
    @TableId(value = "alarm_id", type = IdType.AUTO)
    private Long alarmId;

    @ApiModelProperty("关联设备ID")
    @TableField("device_id")
    private Long deviceId;

    @ApiModelProperty("报警类型(如:消防通道占用)")
    @TableField("alarm_type")
    private String alarmType;

    @ApiModelProperty("风险等级(1:紧急 2:重要 3:一般)")
    @TableField("risk_level")
    private Byte riskLevel;

    @ApiModelProperty("报警位置")
    @TableField("location")
    private String location;

    @ApiModelProperty("报警时间")
    @TableField("alarm_time")
    private LocalDateTime alarmTime;

    @ApiModelProperty("处置状态(0:未处置 1:处置中 2:已完成)")
    @TableField("status")
    private Byte status;

    @ApiModelProperty("现场截图URL")
    @TableField("screenshot_url")
    private String screenshotUrl;

    @ApiModelProperty("关联视频片段URL")
    @TableField("video_url")
    private String videoUrl;

    @ApiModelProperty("记录创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;
}
