package com.smartcampus.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("隐患上报审核请求")
public class AuditReportDto {

    @NotNull
    @ApiModelProperty("上报ID")
    private Long reportId;

    @NotNull
    @ApiModelProperty("审核结果(1:已处理 2:驳回)")
    private Byte auditStatus;

    @ApiModelProperty("审核说明")
    private String auditRemark;
}
