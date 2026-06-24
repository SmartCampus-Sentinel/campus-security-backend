package com.smartcampus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcampus.common.Result;
import com.smartcampus.dto.AlarmDisposalDto;
import com.smartcampus.entity.AlarmDisposal;
import com.smartcampus.service.IAlarmDisposalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/alarmDisposal")
@RequiredArgsConstructor
public class AlarmDisposalController {

    private final IAlarmDisposalService alarmDisposalService;

    @PostMapping
    public Result<AlarmDisposal> submit(@Valid @RequestBody AlarmDisposalDto dto) {
        AlarmDisposal disposal = alarmDisposalService.submit(dto);
        return Result.success(disposal, "处置记录提交成功");
    }

    @GetMapping("/page")
    public Result<Page<AlarmDisposal>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long alarmId) {
        Page<AlarmDisposal> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<AlarmDisposal> wrapper = new LambdaQueryWrapper<>();
        if (alarmId != null) {
            wrapper.eq(AlarmDisposal::getAlarmId, alarmId);
        }
        wrapper.orderByDesc(AlarmDisposal::getDisposalTime);
        return Result.success(alarmDisposalService.page(page, wrapper));
    }

    @GetMapping("/{disposalId}")
    public Result<AlarmDisposal> getById(@PathVariable Long disposalId) {
        AlarmDisposal disposal = alarmDisposalService.getById(disposalId);
        if (disposal == null) {
            return Result.error("处置记录不存在");
        }
        return Result.success(disposal);
    }

    @GetMapping("/byAlarm/{alarmId}")
    public Result<List<AlarmDisposal>> byAlarm(@PathVariable Long alarmId) {
        LambdaQueryWrapper<AlarmDisposal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlarmDisposal::getAlarmId, alarmId);
        wrapper.orderByDesc(AlarmDisposal::getDisposalTime);
        return Result.success(alarmDisposalService.list(wrapper));
    }
}
