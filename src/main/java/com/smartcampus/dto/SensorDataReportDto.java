package com.smartcampus.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@ApiModel("传感器数据上报请求")
public class SensorDataReportDto {

    @NotNull
    @ApiModelProperty("设备ID")
    private Long deviceId;

    @ApiModelProperty("烟雾浓度")
    private BigDecimal smokeConcentration;

    @ApiModelProperty("温度")
    private BigDecimal temperature;
}
