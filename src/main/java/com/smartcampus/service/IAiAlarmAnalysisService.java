package com.smartcampus.service;

import com.smartcampus.dto.AiAnalysisResponse;
import com.smartcampus.entity.AlarmEvent;

/**
 * AI 视觉告警异步研判服务
 */
public interface IAiAlarmAnalysisService {

    /**
     * 异步执行 AI 研判，不阻塞调用线程
     */
    void processAlarmAsync(AlarmEvent event);
}
