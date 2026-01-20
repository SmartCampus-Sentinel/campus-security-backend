package com.smartcampus.service.impl;

import com.smartcampus.entity.AlarmEvent;
import com.smartcampus.mapper.AlarmEventMapper;
import com.smartcampus.service.IAlarmEventService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 报警事件记录表 服务实现类
 * </p>
 *
 * @author SmartCampusTeam
 * @since 2026-01-20
 */
@Service
public class AlarmEventServiceImpl extends ServiceImpl<AlarmEventMapper, AlarmEvent> implements IAlarmEventService {

}
