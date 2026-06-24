package com.smartcampus.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("AI视觉告警上报请求")
public class AiVisualAlarmReport {

    @NotNull
    @ApiModelProperty("设备ID")
    private Long deviceId;

    @NotBlank
    @ApiModelProperty("告警类型: night_intrusion/crowd_gathering/fire_hazard/illegal_parking")
    private String alarmType;

    @ApiModelProperty("风险等级(1:紧急 2:重要 3:一般), 不传则由AI研判")
    private Byte riskLevel;

    @NotBlank
    @ApiModelProperty("告警位置描述")
    private String location;

    @ApiModelProperty("现场截图URL")
    private String screenshotUrl;

    @ApiModelProperty("关联视频片段URL")
    private String videoUrl;
}
