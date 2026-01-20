package com.smartcampus.service.impl;

import com.smartcampus.entity.SensorData;
import com.smartcampus.mapper.SensorDataMapper;
import com.smartcampus.service.ISensorDataService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 传感器实时数据表 服务实现类
 * </p>
 *
 * @author SmartCampusTeam
 * @since 2026-01-20
 */
@Service
public class SensorDataServiceImpl extends ServiceImpl<SensorDataMapper, SensorData> implements ISensorDataService {

}
