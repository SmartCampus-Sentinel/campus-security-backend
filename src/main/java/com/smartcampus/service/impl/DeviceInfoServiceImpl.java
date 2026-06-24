package com.smartcampus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartcampus.entity.DeviceInfo;
import com.smartcampus.mapper.DeviceInfoMapper;
import com.smartcampus.service.IDeviceInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeviceInfoServiceImpl extends ServiceImpl<DeviceInfoMapper, DeviceInfo> implements IDeviceInfoService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String STATS_CACHE_KEY = "device:stats";

    @Override
    public void checkDeviceCodeUnique(String deviceCode, Long excludeDeviceId) {
        LambdaQueryWrapper<DeviceInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeviceInfo::getDeviceCode, deviceCode);
        if (excludeDeviceId != null) {
            wrapper.ne(DeviceInfo::getDeviceId, excludeDeviceId);
        }
        if (this.count(wrapper) > 0) {
            throw new RuntimeException("设备编号「" + deviceCode + "」已存在");
        }
    }

    @Override
    public void heartbeat(Long deviceId) {
        DeviceInfo device = this.getById(deviceId);
        if (device == null) {
            throw new RuntimeException("设备不存在: " + deviceId);
        }
        device.setHeartbeatTime(LocalDateTime.now());
        device.setStatus(true);
        this.updateById(device);
        redisTemplate.delete(STATS_CACHE_KEY);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getStats() {
        Object cached = redisTemplate.opsForValue().get(STATS_CACHE_KEY);
        if (cached instanceof Map) {
            return (Map<String, Object>) cached;
        }

        Map<String, Object> stats = new HashMap<>();
        long total = this.count();
        long online = this.count(new LambdaQueryWrapper<DeviceInfo>().eq(DeviceInfo::getStatus, true));
        long offline = total - online;

        stats.put("total", total);
        stats.put("online", online);
        stats.put("offline", offline);

        Map<String, Long> byType = this.list().stream()
                .collect(Collectors.groupingBy(
                        d -> d.getDeviceType() != null ? d.getDeviceType() : "unknown",
                        Collectors.counting()));
        stats.put("byType", byType);

        redisTemplate.opsForValue().set(STATS_CACHE_KEY, stats, 30, TimeUnit.SECONDS);

        return stats;
    }
}
