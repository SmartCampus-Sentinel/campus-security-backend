package com.smartcampus.service;

import cn.hutool.json.JSONUtil;
import com.smartcampus.dto.SensorDataReportDto;
import com.smartcampus.entity.DeviceInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MqttMessageHandler {

    private final ISensorDataService sensorDataService;
    private final IDeviceInfoService deviceInfoService;

    public void handle(String topic, String payload) {
        if (topic.startsWith("campus/sensor/")) {
            handleSensorData(payload);
        } else if (topic.startsWith("campus/camera/")) {
            handleCameraEvent(payload);
        } else {
            log.warn("[MQTT] 未知主题: {}", topic);
        }
    }

    private void handleSensorData(String payload) {
        try {
            Map<String, Object> data = JSONUtil.parseObj(payload);
            Long deviceId = Long.valueOf(data.get("deviceId").toString());

            DeviceInfo device = deviceInfoService.getById(deviceId);
            if (device == null) {
                log.warn("[MQTT] 传感器设备不存在: deviceId={}", deviceId);
                return;
            }

            deviceInfoService.heartbeat(deviceId);

            SensorDataReportDto dto = new SensorDataReportDto();
            dto.setDeviceId(deviceId);
            if (data.containsKey("smokeConcentration")) {
                dto.setSmokeConcentration(new BigDecimal(data.get("smokeConcentration").toString()));
            }
            if (data.containsKey("temperature")) {
                dto.setTemperature(new BigDecimal(data.get("temperature").toString()));
            }

            sensorDataService.report(dto);
            log.info("[MQTT] 传感器数据处理完成 deviceId={}", deviceId);
        } catch (Exception e) {
            log.error("[MQTT] 传感器数据处理失败: {}", payload, e);
        }
    }

    private void handleCameraEvent(String payload) {
        try {
            Map<String, Object> data = JSONUtil.parseObj(payload);
            Long deviceId = Long.valueOf(data.get("deviceId").toString());
            String type = (String) data.get("type");

            if ("heartbeat".equals(type)) {
                deviceInfoService.heartbeat(deviceId);
                log.info("[MQTT] 摄像头心跳 deviceId={}", deviceId);
            } else {
                log.info("[MQTT] 摄像头事件 deviceId={}, type={}", deviceId, type);
            }
        } catch (Exception e) {
            log.error("[MQTT] 摄像头事件处理失败: {}", payload, e);
        }
    }
}
