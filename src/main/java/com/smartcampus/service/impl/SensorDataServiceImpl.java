package com.smartcampus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartcampus.dto.SensorDataReportDto;
import com.smartcampus.entity.AlarmEvent;
import com.smartcampus.entity.DeviceInfo;
import com.smartcampus.entity.SensorData;
import com.smartcampus.mapper.SensorDataMapper;
import com.smartcampus.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class SensorDataServiceImpl extends ServiceImpl<SensorDataMapper, SensorData> implements ISensorDataService {

    private static final BigDecimal SMOKE_THRESHOLD = new BigDecimal("300");
    private static final BigDecimal TEMP_THRESHOLD = new BigDecimal("60");

    private final IAlarmEventService alarmEventService;
    private final IAiAlarmAnalysisService aiAlarmAnalysisService;
    private final IDeviceInfoService deviceInfoService;
    private final WebSocketPushService webSocketPushService;

    @Override
    public SensorData report(SensorDataReportDto dto) {
        SensorData data = new SensorData();
        data.setDeviceId(dto.getDeviceId());
        data.setSmokeConcentration(dto.getSmokeConcentration());
        data.setTemperature(dto.getTemperature());
        data.setCollectTime(LocalDateTime.now());

        boolean abnormal = isAbnormal(dto.getSmokeConcentration(), dto.getTemperature());
        data.setIsAbnormal(abnormal);

        this.save(data);

        if (abnormal) {
            triggerAlarm(data);
        }

        return data;
    }

    @Override
    public SensorData getLatestByDeviceId(Long deviceId) {
        LambdaQueryWrapper<SensorData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SensorData::getDeviceId, deviceId);
        wrapper.orderByDesc(SensorData::getCollectTime);
        wrapper.last("LIMIT 1");
        return this.getOne(wrapper);
    }

    private boolean isAbnormal(BigDecimal smoke, BigDecimal temperature) {
        if (smoke != null && smoke.compareTo(SMOKE_THRESHOLD) > 0) {
            return true;
        }
        if (temperature != null && temperature.compareTo(TEMP_THRESHOLD) > 0) {
            return true;
        }
        return false;
    }

    private void triggerAlarm(SensorData data) {
        try {
            DeviceInfo device = deviceInfoService.getById(data.getDeviceId());
            String location = device != null ? device.getLocation() : "未知位置";

            AlarmEvent event = new AlarmEvent();
            event.setDeviceId(data.getDeviceId());
            event.setAlarmType("sensor_abnormal");
            event.setRiskLevel(estimateRiskLevel(data));
            event.setLocation(location);
            event.setAlarmTime(LocalDateTime.now());
            event.setStatus((byte) 0);
            event.setCreateTime(LocalDateTime.now());
            alarmEventService.save(event);

            webSocketPushService.pushAlarm(event);

            aiAlarmAnalysisService.processAlarmAsync(event);

            log.info("[传感器告警] 设备异常触发告警 deviceId={}, alarmId={}, smoke={}, temp={}",
                    data.getDeviceId(), event.getAlarmId(),
                    data.getSmokeConcentration(), data.getTemperature());
        } catch (Exception e) {
            log.error("[传感器告警] 触发告警失败 deviceId={}", data.getDeviceId(), e);
        }
    }

    private Byte estimateRiskLevel(SensorData data) {
        if (data.getSmokeConcentration() != null && data.getSmokeConcentration().compareTo(new BigDecimal("500")) > 0) {
            return 1;
        }
        if (data.getTemperature() != null && data.getTemperature().compareTo(new BigDecimal("80")) > 0) {
            return 1;
        }
        return 2;
    }
}
