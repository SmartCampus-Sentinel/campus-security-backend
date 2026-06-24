package com.smartcampus.service;

import com.smartcampus.dto.AlarmDisposalDto;
import com.smartcampus.entity.AlarmDisposal;
import com.smartcampus.entity.AlarmEvent;
import com.smartcampus.mapper.AlarmDisposalMapper;
import com.smartcampus.service.impl.AlarmDisposalServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlarmDisposalServiceImplTest {

    @Mock
    private AlarmDisposalMapper alarmDisposalMapper;

    @Mock
    private IAlarmEventService alarmEventService;

    @Spy
    @InjectMocks
    private AlarmDisposalServiceImpl alarmDisposalService;

    @Test
    void submit_success() {
        AlarmEvent event = new AlarmEvent();
        event.setAlarmId(1L);
        event.setStatus((byte) 0);

        when(alarmEventService.getById(1L)).thenReturn(event);
        doReturn(true).when(alarmDisposalService).save(any(AlarmDisposal.class));
        when(alarmEventService.updateById(any(AlarmEvent.class))).thenReturn(true);

        AlarmDisposalDto dto = new AlarmDisposalDto();
        dto.setAlarmId(1L);
        dto.setDisposerId(10L);
        dto.setDisposalContent("已处理");

        AlarmDisposal result = alarmDisposalService.submit(dto);

        assertNotNull(result);
        assertEquals(1L, result.getAlarmId());
        assertEquals(10L, result.getDisposerId());
        assertNotNull(result.getDisposalTime());

        ArgumentCaptor<AlarmEvent> eventCaptor = ArgumentCaptor.forClass(AlarmEvent.class);
        verify(alarmEventService).updateById(eventCaptor.capture());
        assertEquals(Byte.valueOf((byte) 2), eventCaptor.getValue().getStatus());
    }

    @Test
    void submit_alarm_not_found() {
        when(alarmEventService.getById(99L)).thenReturn(null);

        AlarmDisposalDto dto = new AlarmDisposalDto();
        dto.setAlarmId(99L);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> alarmDisposalService.submit(dto));
        assertTrue(ex.getMessage().contains("报警事件不存在"));

        verify(alarmDisposalService, never()).save(any());
    }
}
