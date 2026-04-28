package com.smartcampus.controller;

import com.smartcampus.common.Result;
import com.smartcampus.dto.AiVisualAlarmReport;
import com.smartcampus.entity.AlarmEvent;
import com.smartcampus.service.IAlarmEventService;
import com.smartcampus.service.IAiAlarmAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/alarmEvent")
@RequiredArgsConstructor
public class AlarmEventController {

    private final IAlarmEventService alarmEventService;
    private final IAiAlarmAnalysisService aiAlarmAnalysisService;

    /**
     * 监控探头上报AI视觉告警。
     * 快速落库后立即返回，AI研判异步执行不阻塞响应。
     */
    @PostMapping("/report")
    public Result<Long> reportAlarm(@Validated @RequestBody AiVisualAlarmReport report) {
        AlarmEvent event = new AlarmEvent();
        event.setDeviceId(report.getDeviceId());
        event.setAlarmType(report.getAlarmType());
        event.setRiskLevel(report.getRiskLevel() != null ? report.getRiskLevel() : 3);
        event.setLocation(report.getLocation());
        event.setScreenshotUrl(report.getScreenshotUrl());
        event.setVideoUrl(report.getVideoUrl());
        event.setAlarmTime(LocalDateTime.now());
        event.setStatus((byte) 0);
        event.setCreateTime(LocalDateTime.now());

        alarmEventService.save(event);

        aiAlarmAnalysisService.processAlarmAsync(event);

        return Result.success(event.getAlarmId(), "告警已提交，AI研判中");
    }
}
