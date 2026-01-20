package com.smartcampus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 传感器实时数据表
 * </p>
 *
 * @author SmartCampusTeam
 * @since 2026-01-20
 */
@Getter
@Setter
@TableName("sensor_data")
@ApiModel(value = "SensorData对象", description = "传感器实时数据表")
public class SensorData implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("数据ID")
    @TableId(value = "data_id", type = IdType.AUTO)
    private Long dataId;

    @ApiModelProperty("设备ID")
    @TableField("device_id")
    private Long deviceId;

    @ApiModelProperty("烟雾浓度")
    @TableField("smoke_concentration")
    private BigDecimal smokeConcentration;

    @ApiModelProperty("温度")
    @TableField("temperature")
    private BigDecimal temperature;

    @ApiModelProperty("是否异常(1:是 0:否)")
    @TableField("is_abnormal")
    private Boolean isAbnormal;

    @ApiModelProperty("采集时间")
    @TableField("collect_time")
    private LocalDateTime collectTime;
}
