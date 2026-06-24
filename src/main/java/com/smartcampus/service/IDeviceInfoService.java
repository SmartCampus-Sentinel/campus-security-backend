package com.smartcampus.service;

import com.smartcampus.entity.DeviceInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface IDeviceInfoService extends IService<DeviceInfo> {

    void checkDeviceCodeUnique(String deviceCode, Long excludeDeviceId);

    void heartbeat(Long deviceId);

    Map<String, Object> getStats();
}
