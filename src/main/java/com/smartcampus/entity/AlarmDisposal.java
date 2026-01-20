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
 * 报警处置记录表
 * </p>
 *
 * @author SmartCampusTeam
 * @since 2026-01-20
 */
@Getter
@Setter
@TableName("alarm_disposal")
@ApiModel(value = "AlarmDisposal对象", description = "报警处置记录表")
public class AlarmDisposal implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("处置ID")
    @TableId(value = "disposal_id", type = IdType.AUTO)
    private Long disposalId;

    @ApiModelProperty("关联报警ID")
    @TableField("alarm_id")
    private Long alarmId;

    @ApiModelProperty("处置人ID(User ID)")
    @TableField("disposer_id")
    private Long disposerId;

    @ApiModelProperty("处置时间")
    @TableField("disposal_time")
    private LocalDateTime disposalTime;

    @ApiModelProperty("处置说明")
    @TableField("disposal_content")
    private String disposalContent;

    @ApiModelProperty("处置结果照片URL")
    @TableField("result_photo_url")
    private String resultPhotoUrl;
}
