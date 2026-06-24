package com.smartcampus.service.impl;

import com.smartcampus.dto.AlarmDisposalDto;
import com.smartcampus.entity.AlarmDisposal;
import com.smartcampus.entity.AlarmEvent;
import com.smartcampus.mapper.AlarmDisposalMapper;
import com.smartcampus.service.IAlarmDisposalService;
import com.smartcampus.service.IAlarmEventService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AlarmDisposalServiceImpl extends ServiceImpl<AlarmDisposalMapper, AlarmDisposal> implements IAlarmDisposalService {

    private final IAlarmEventService alarmEventService;

    @Override
    @Transactional
    public AlarmDisposal submit(AlarmDisposalDto dto) {
        AlarmEvent event = alarmEventService.getById(dto.getAlarmId());
        if (event == null) {
            throw new RuntimeException("报警事件不存在: " + dto.getAlarmId());
        }

        AlarmDisposal disposal = new AlarmDisposal();
        disposal.setAlarmId(dto.getAlarmId());
        disposal.setDisposerId(dto.getDisposerId());
        disposal.setDisposalContent(dto.getDisposalContent());
        disposal.setResultPhotoUrl(dto.getResultPhotoUrl());
        disposal.setDisposalTime(LocalDateTime.now());
        this.save(disposal);

        event.setStatus((byte) 2);
        alarmEventService.updateById(event);

        return disposal;
    }
}
