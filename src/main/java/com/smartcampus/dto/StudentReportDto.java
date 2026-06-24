package com.smartcampus.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("学生隐患上报请求")
public class StudentReportDto {

    @NotBlank
    @ApiModelProperty("学生ID")
    private String studentId;

    @NotBlank
    @ApiModelProperty("隐患类型")
    private String reportType;

    @NotBlank
    @ApiModelProperty("位置描述")
    private String location;

    @ApiModelProperty("详细描述")
    private String description;

    @ApiModelProperty("多媒体文件URL(逗号分隔)")
    private String mediaUrl;
}
