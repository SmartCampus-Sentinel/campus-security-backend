package com.smartcampus.service;

import com.smartcampus.dto.SensorDataReportDto;
import com.smartcampus.entity.AlarmEvent;
import com.smartcampus.entity.DeviceInfo;
import com.smartcampus.entity.SensorData;
import com.smartcampus.mapper.SensorDataMapper;
import com.smartcampus.service.impl.SensorDataServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SensorDataServiceImplTest {

    @Mock
    private SensorDataMapper sensorDataMapper;

    @Mock
    private IAlarmEventService alarmEventService;

    @Mock
    private IAiAlarmAnalysisService aiAlarmAnalysisService;

    @Mock
    private IDeviceInfoService deviceInfoService;

    @Mock
    private WebSocketPushService webSocketPushService;

    @Spy
    @InjectMocks
    private SensorDataServiceImpl sensorDataService;

    @Test
    void report_normal_data() {
        SensorDataReportDto dto = new SensorDataReportDto();
        dto.setDeviceId(1L);
        dto.setSmokeConcentration(new BigDecimal("100"));
        dto.setTemperature(new BigDecimal("25"));

        doReturn(true).when(sensorDataService).save(any(SensorData.class));

        SensorData result = sensorDataService.report(dto);

        assertFalse(result.getIsAbnormal());
        verify(alarmEventService, never()).save(any());
    }

    @Test
    void report_smoke_abnormal() {
        SensorDataReportDto dto = new SensorDataReportDto();
        dto.setDeviceId(1L);
        dto.setSmokeConcentration(new BigDecimal("350"));
        dto.setTemperature(new BigDecimal("25"));

        DeviceInfo device = new DeviceInfo();
        device.setDeviceId(1L);
        device.setLocation("实验楼A区");

        doReturn(true).when(sensorDataService).save(any(SensorData.class));
        when(deviceInfoService.getById(1L)).thenReturn(device);
        when(alarmEventService.save(any(AlarmEvent.class))).thenReturn(true);

        SensorData result = sensorDataService.report(dto);

        assertTrue(result.getIsAbnormal());
        verify(alarmEventService).save(any(AlarmEvent.class));
        verify(aiAlarmAnalysisService).processAlarmAsync(any(AlarmEvent.class));
    }

    @Test
    void report_temp_abnormal() {
        SensorDataReportDto dto = new SensorDataReportDto();
        dto.setDeviceId(1L);
        dto.setSmokeConcentration(new BigDecimal("50"));
        dto.setTemperature(new BigDecimal("65"));

        DeviceInfo device = new DeviceInfo();
        device.setDeviceId(1L);
        device.setLocation("图书馆");

        doReturn(true).when(sensorDataService).save(any(SensorData.class));
        when(deviceInfoService.getById(1L)).thenReturn(device);
        when(alarmEventService.save(any(AlarmEvent.class))).thenReturn(true);

        SensorData result = sensorDataService.report(dto);

        assertTrue(result.getIsAbnormal());
        verify(alarmEventService).save(any(AlarmEvent.class));
    }

    @Test
    void report_high_risk() {
        SensorDataReportDto dto = new SensorDataReportDto();
        dto.setDeviceId(1L);
        dto.setSmokeConcentration(new BigDecimal("550"));
        dto.setTemperature(new BigDecimal("25"));

        DeviceInfo device = new DeviceInfo();
        device.setDeviceId(1L);
        device.setLocation("实验室");

        doReturn(true).when(sensorDataService).save(any(SensorData.class));
        when(deviceInfoService.getById(1L)).thenReturn(device);
        when(alarmEventService.save(any(AlarmEvent.class))).thenReturn(true);

        sensorDataService.report(dto);

        ArgumentCaptor<AlarmEvent> captor = ArgumentCaptor.forClass(AlarmEvent.class);
        verify(alarmEventService).save(captor.capture());
        assertEquals(Byte.valueOf((byte) 1), captor.getValue().getRiskLevel());
    }

    @Test
    void report_null_values() {
        SensorDataReportDto dto = new SensorDataReportDto();
        dto.setDeviceId(1L);
        dto.setSmokeConcentration(null);
        dto.setTemperature(null);

        doReturn(true).when(sensorDataService).save(any(SensorData.class));

        SensorData result = sensorDataService.report(dto);

        assertFalse(result.getIsAbnormal());
        verify(alarmEventService, never()).save(any());
    }
}
