package com.smartcampus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcampus.common.Result;
import com.smartcampus.dto.SensorDataReportDto;
import com.smartcampus.entity.SensorData;
import com.smartcampus.service.ISensorDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/sensorData")
@RequiredArgsConstructor
public class SensorDataController {

    private final ISensorDataService sensorDataService;

    @GetMapping("/page")
    public Result<Page<SensorData>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long deviceId,
            @RequestParam(required = false) Boolean isAbnormal,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        Page<SensorData> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SensorData> wrapper = new LambdaQueryWrapper<>();
        if (deviceId != null) {
            wrapper.eq(SensorData::getDeviceId, deviceId);
        }
        if (isAbnormal != null) {
            wrapper.eq(SensorData::getIsAbnormal, isAbnormal);
        }
        if (startTime != null) {
            wrapper.ge(SensorData::getCollectTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(SensorData::getCollectTime, endTime);
        }
        wrapper.orderByDesc(SensorData::getCollectTime);
        return Result.success(sensorDataService.page(page, wrapper));
    }

    @GetMapping("/{dataId}")
    public Result<SensorData> getById(@PathVariable Long dataId) {
        SensorData data = sensorDataService.getById(dataId);
        if (data == null) {
            return Result.error("数据不存在");
        }
        return Result.success(data);
    }

    @PostMapping("/report")
    public Result<SensorData> report(@Valid @RequestBody SensorDataReportDto dto) {
        SensorData data = sensorDataService.report(dto);
        return Result.success(data, "数据上报成功");
    }

    @GetMapping("/latest/{deviceId}")
    public Result<SensorData> latest(@PathVariable Long deviceId) {
        SensorData data = sensorDataService.getLatestByDeviceId(deviceId);
        if (data == null) {
            return Result.error("该设备暂无数据");
        }
        return Result.success(data);
    }

    @GetMapping("/abnormal")
    public Result<List<SensorData>> abnormal() {
        LambdaQueryWrapper<SensorData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SensorData::getIsAbnormal, true);
        wrapper.orderByDesc(SensorData::getCollectTime);
        return Result.success(sensorDataService.list(wrapper));
    }
}
