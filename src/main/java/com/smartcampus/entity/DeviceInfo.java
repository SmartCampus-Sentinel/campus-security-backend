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
 * 设备信息表
 * </p>
 *
 * @author SmartCampusTeam
 * @since 2026-01-20
 */
@Getter
@Setter
@TableName("device_info")
@ApiModel(value = "DeviceInfo对象", description = "设备信息表")
public class DeviceInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("设备ID")
    @TableId(value = "device_id", type = IdType.AUTO)
    private Long deviceId;

    @ApiModelProperty("设备编号(唯一标识)")
    @TableField("device_code")
    private String deviceCode;

    @ApiModelProperty("类型(camera:摄像头, sensor:传感器)")
    @TableField("device_type")
    private String deviceType;

    @ApiModelProperty("安装位置")
    @TableField("location")
    private String location;

    @ApiModelProperty("IP地址")
    @TableField("ip_address")
    private String ipAddress;

    @ApiModelProperty("状态(1:在线 0:离线)")
    @TableField("status")
    private Boolean status;

    @ApiModelProperty("最后心跳时间")
    @TableField("heartbeat_time")
    private LocalDateTime heartbeatTime;

    @ApiModelProperty("安装时间")
    @TableField("create_time")
    private LocalDateTime createTime;
}
