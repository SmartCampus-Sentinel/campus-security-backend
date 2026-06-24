package com.smartcampus.service;

import com.smartcampus.dto.SensorDataReportDto;
import com.smartcampus.entity.SensorData;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ISensorDataService extends IService<SensorData> {

    SensorData report(SensorDataReportDto dto);

    SensorData getLatestByDeviceId(Long deviceId);
}
