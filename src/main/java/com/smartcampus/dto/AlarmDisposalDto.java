package com.smartcampus.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("报警处置提交请求")
public class AlarmDisposalDto {

    @NotNull
    @ApiModelProperty("关联报警ID")
    private Long alarmId;

    @ApiModelProperty("处置人ID")
    private Long disposerId;

    @ApiModelProperty("处置说明")
    private String disposalContent;

    @ApiModelProperty("处置结果照片URL")
    private String resultPhotoUrl;
}
