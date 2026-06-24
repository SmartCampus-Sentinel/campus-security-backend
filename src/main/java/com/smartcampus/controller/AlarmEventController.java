package com.smartcampus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcampus.common.Result;
import com.smartcampus.dto.AiVisualAlarmReport;
import com.smartcampus.entity.AlarmEvent;
import com.smartcampus.service.IAlarmEventService;
import com.smartcampus.service.IAiAlarmAnalysisService;
import com.smartcampus.service.WebSocketPushService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/alarmEvent")
@RequiredArgsConstructor
public class AlarmEventController {

    private final IAlarmEventService alarmEventService;
    private final IAiAlarmAnalysisService aiAlarmAnalysisService;
    private final WebSocketPushService webSocketPushService;

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
        webSocketPushService.pushAlarm(event);
        aiAlarmAnalysisService.processAlarmAsync(event);

        return Result.success(event.getAlarmId(), "告警已提交，AI研判中");
    }

    @GetMapping("/page")
    public Result<Page<AlarmEvent>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Byte status,
            @RequestParam(required = false) Byte riskLevel,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        Page<AlarmEvent> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<AlarmEvent> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(AlarmEvent::getStatus, status);
        }
        if (riskLevel != null) {
            wrapper.eq(AlarmEvent::getRiskLevel, riskLevel);
        }
        if (startTime != null) {
            wrapper.ge(AlarmEvent::getAlarmTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(AlarmEvent::getAlarmTime, endTime);
        }
        wrapper.orderByDesc(AlarmEvent::getCreateTime);
        return Result.success(alarmEventService.page(page, wrapper));
    }

    @GetMapping("/{alarmId}")
    public Result<AlarmEvent> getById(@PathVariable Long alarmId) {
        AlarmEvent event = alarmEventService.getById(alarmId);
        if (event == null) {
            return Result.error("告警不存在");
        }
        return Result.success(event);
    }

    @PutMapping("/status")
    public Result<Void> updateStatus(@RequestParam Long alarmId, @RequestParam Byte status) {
        AlarmEvent event = alarmEventService.getById(alarmId);
        if (event == null) {
            return Result.error("告警不存在");
        }
        event.setStatus(status);
        alarmEventService.updateById(event);
        return Result.success(null, "状态更新成功");
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> stats = new java.util.HashMap<>();

        long total = alarmEventService.count();
        long untreated = alarmEventService.count(new LambdaQueryWrapper<AlarmEvent>().eq(AlarmEvent::getStatus, 0));
        long processing = alarmEventService.count(new LambdaQueryWrapper<AlarmEvent>().eq(AlarmEvent::getStatus, 1));
        long completed = alarmEventService.count(new LambdaQueryWrapper<AlarmEvent>().eq(AlarmEvent::getStatus, 2));

        stats.put("total", total);
        stats.put("untreated", untreated);
        stats.put("processing", processing);
        stats.put("completed", completed);

        return Result.success(stats);
    }
}
