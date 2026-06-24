package com.smartcampus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcampus.common.Result;
import com.smartcampus.entity.DeviceInfo;
import com.smartcampus.service.IDeviceInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/deviceInfo")
@RequiredArgsConstructor
public class DeviceInfoController {

    private final IDeviceInfoService deviceInfoService;

    @GetMapping("/page")
    public Result<Page<DeviceInfo>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String deviceType,
            @RequestParam(required = false) Boolean status) {
        Page<DeviceInfo> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<DeviceInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(deviceType)) {
            wrapper.eq(DeviceInfo::getDeviceType, deviceType);
        }
        if (status != null) {
            wrapper.eq(DeviceInfo::getStatus, status);
        }
        wrapper.orderByDesc(DeviceInfo::getCreateTime);
        return Result.success(deviceInfoService.page(page, wrapper));
    }

    @GetMapping("/{deviceId}")
    public Result<DeviceInfo> getById(@PathVariable Long deviceId) {
        DeviceInfo device = deviceInfoService.getById(deviceId);
        if (device == null) {
            return Result.error("设备不存在");
        }
        return Result.success(device);
    }

    @PostMapping
    public Result<Void> add(@RequestBody DeviceInfo deviceInfo) {
        deviceInfoService.checkDeviceCodeUnique(deviceInfo.getDeviceCode(), null);
        deviceInfo.setCreateTime(LocalDateTime.now());
        deviceInfo.setStatus(false);
        deviceInfoService.save(deviceInfo);
        return Result.success(null, "新增成功");
    }

    @PutMapping
    public Result<Void> update(@RequestBody DeviceInfo deviceInfo) {
        if (deviceInfo.getDeviceId() == null) {
            return Result.error("设备ID不能为空");
        }
        if (StringUtils.hasText(deviceInfo.getDeviceCode())) {
            deviceInfoService.checkDeviceCodeUnique(deviceInfo.getDeviceCode(), deviceInfo.getDeviceId());
        }
        deviceInfoService.updateById(deviceInfo);
        return Result.success(null, "修改成功");
    }

    @DeleteMapping("/{deviceId}")
    public Result<Void> delete(@PathVariable Long deviceId) {
        deviceInfoService.removeById(deviceId);
        return Result.success(null, "删除成功");
    }

    @PutMapping("/heartbeat/{deviceId}")
    public Result<Void> heartbeat(@PathVariable Long deviceId) {
        deviceInfoService.heartbeat(deviceId);
        return Result.success(null, "心跳更新成功");
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        return Result.success(deviceInfoService.getStats());
    }
}
