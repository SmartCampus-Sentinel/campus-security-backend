package com.smartcampus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@TableName("t_sensor_data")
@ApiModel(value = "SensorData对象", description = "传感器数据表")
public class SensorData {

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "数据ID")
    private Integer id;

    @ApiModelProperty(value = "传感器ID")
    private String sensorId;

    @ApiModelProperty(value = "传感器类型")
    private String sensorType;

    @ApiModelProperty(value = "传感器值")
    private Double value;

    @ApiModelProperty(value = "单位")
    private String unit;

    @ApiModelProperty(value = "采集时间")
    private String timestamp;

    @ApiModelProperty(value = "位置信息")
    private String location;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "备注")
    private String remark;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public String getSensorType() {
        return sensorType;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
