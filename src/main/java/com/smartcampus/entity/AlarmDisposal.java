package com.smartcampus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@TableName("t_alarm_disposal")
@ApiModel(value = "AlarmDisposal对象", description = "告警处置表")
public class AlarmDisposal {

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "处置ID")
    private Integer id;

    @ApiModelProperty(value = "告警事件ID")
    private Integer alarmEventId;

    @ApiModelProperty(value = "处置人员ID")
    private Integer userId;

    @ApiModelProperty(value = "处置内容")
    private String disposalContent;

    @ApiModelProperty(value = "处置结果")
    private String disposalResult;

    @ApiModelProperty(value = "处置时间")
    private String disposalTime;

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

    public Integer getAlarmEventId() {
        return alarmEventId;
    }

    public void setAlarmEventId(Integer alarmEventId) {
        this.alarmEventId = alarmEventId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getDisposalContent() {
        return disposalContent;
    }

    public void setDisposalContent(String disposalContent) {
        this.disposalContent = disposalContent;
    }

    public String getDisposalResult() {
        return disposalResult;
    }

    public void setDisposalResult(String disposalResult) {
        this.disposalResult = disposalResult;
    }

    public String getDisposalTime() {
        return disposalTime;
    }

    public void setDisposalTime(String disposalTime) {
        this.disposalTime = disposalTime;
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
