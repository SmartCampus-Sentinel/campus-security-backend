package com.smartcampus.service;

import com.smartcampus.entity.AlarmEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketPushService {

    private final SimpMessagingTemplate messagingTemplate;

    public void pushAlarm(AlarmEvent event) {
        try {
            messagingTemplate.convertAndSend("/topic/alarm", event);
            log.info("[WebSocket] 推送告警 alarmId={}, type={}", event.getAlarmId(), event.getAlarmType());
        } catch (Exception e) {
            log.error("[WebSocket] 推送告警失败 alarmId={}", event.getAlarmId(), e);
        }
    }

    public void pushDeviceStatus(Long deviceId, boolean online) {
        try {
            messagingTemplate.convertAndSend("/topic/device/status",
                    java.util.Map.of("deviceId", deviceId, "online", online));
            log.info("[WebSocket] 推送设备状态 deviceId={}, online={}", deviceId, online);
        } catch (Exception e) {
            log.error("[WebSocket] 推送设备状态失败 deviceId={}", deviceId, e);
        }
    }
}
