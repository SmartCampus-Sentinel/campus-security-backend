package com.smartcampus.service.impl;

import com.smartcampus.entity.DeviceInfo;
import com.smartcampus.mapper.DeviceInfoMapper;
import com.smartcampus.service.IDeviceInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 设备信息表 服务实现类
 * </p>
 *
 * @author SmartCampusTeam
 * @since 2026-01-20
 */
@Service
public class DeviceInfoServiceImpl extends ServiceImpl<DeviceInfoMapper, DeviceInfo> implements IDeviceInfoService {

}
