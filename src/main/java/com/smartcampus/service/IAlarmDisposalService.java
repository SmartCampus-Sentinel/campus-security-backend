package com.smartcampus.service;

import com.smartcampus.dto.AlarmDisposalDto;
import com.smartcampus.entity.AlarmDisposal;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IAlarmDisposalService extends IService<AlarmDisposal> {

    AlarmDisposal submit(AlarmDisposalDto dto);
}
