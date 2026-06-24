package com.smartcampus.service.impl;

import cn.hutool.json.JSONUtil;
import com.smartcampus.dto.AiAnalysisResponse;
import com.smartcampus.entity.AlarmDisposal;
import com.smartcampus.entity.AlarmEvent;
import com.smartcampus.service.IAiAlarmAnalysisService;
import com.smartcampus.service.IAlarmDisposalService;
import com.smartcampus.service.IAlarmEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiAlarmAnalysisServiceImpl implements IAiAlarmAnalysisService {

    private final WebClient webClient;
    private final IAlarmEventService alarmEventService;
    private final IAlarmDisposalService alarmDisposalService;

    @Value("${ai.llm.endpoint}")
    private String llmEndpoint;

    @Value("${ai.llm.api-key}")
    private String apiKey;

    @Value("${ai.llm.model}")
    private String model;

    @Value("${ai.llm.timeout-seconds:60}")
    private int timeoutSeconds;

    @Override
    @Async("aiAlarmExecutor")
    public void processAlarmAsync(AlarmEvent event) {
        long start = System.currentTimeMillis();
        log.info("[AI研判] 开始处理告警 alarmId={}, type={}, deviceId={}",
                event.getAlarmId(), event.getAlarmType(), event.getDeviceId());

        try {
            AiAnalysisResponse result = callLlmForAnalysis(event);
            applyAnalysisResult(event, result);
            long elapsed = System.currentTimeMillis() - start;
            log.info("[AI研判] 完成 alarmId={}, 耗时={}ms, isRealAlarm={}, riskLevel={}",
                    event.getAlarmId(), elapsed, result.getIsRealAlarm(), result.getRiskLevel());
        } catch (Exception e) {
            log.error("[AI研判] 失败 alarmId={}, 保持原始状态待人工处理", event.getAlarmId(), e);
        }
    }

    private AiAnalysisResponse callLlmForAnalysis(AlarmEvent event) {
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", new Object[]{
                        Map.of("role", "system", "content", buildSystemPrompt()),
                        Map.of("role", "user", "content", buildUserPrompt(event))
                },
                "temperature", 0.3
        );

        String response = webClient.post()
                .uri(llmEndpoint)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        resp -> Mono.error(new RuntimeException("LLM API error: " + resp.statusCode())))
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .block();

        return parseLlmResponse(response, event.getAlarmId());
    }

    private String buildSystemPrompt() {
        return "你是一个校园安防监控系统的AI研判助手。你的任务是根据监控探头上报的告警信息，判断是否为真实安全事件，并给出风险等级和处置建议。\n"
                + "请严格按以下JSON格式回复，不要附加其他内容：\n"
                + "{\"isRealAlarm\": true/false, \"riskLevel\": 1-3, \"analysisSummary\": \"分析摘要\", \"suggestedAction\": \"建议处置措施\"}\n"
                + "风险等级: 1=紧急(需立即响应), 2=重要(需尽快处理), 3=一般(可延后处理)";
    }

    private String buildUserPrompt(AlarmEvent event) {
        return String.format(
                "请分析以下校园安防告警：\n- 告警类型: %s\n- 位置: %s\n- 时间: %s\n- 截图URL: %s\n- 视频URL: %s",
                event.getAlarmType(),
                event.getLocation(),
                event.getAlarmTime(),
                event.getScreenshotUrl() != null ? event.getScreenshotUrl() : "无",
                event.getVideoUrl() != null ? event.getVideoUrl() : "无"
        );
    }

    private AiAnalysisResponse parseLlmResponse(String response, Long alarmId) {
        try {
            String json = extractJson(response);
            return JSONUtil.toBean(json, AiAnalysisResponse.class);
        } catch (Exception e) {
            log.warn("[AI研判] 解析LLM响应失败 alarmId={}, 使用默认值。原始响应: {}", alarmId, response);
            AiAnalysisResponse fallback = new AiAnalysisResponse();
            fallback.setIsRealAlarm(false);
            fallback.setRiskLevel(3);
            fallback.setAnalysisSummary("AI研判解析失败，需人工复核");
            fallback.setSuggestedAction("请安防人员查看告警详情并人工判断");
            return fallback;
        }
    }

    private String extractJson(String response) {
        int start = response.indexOf('{');
        int end = response.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return response.substring(start, end + 1);
        }
        return response;
    }

    private void applyAnalysisResult(AlarmEvent event, AiAnalysisResponse result) {
        event.setRiskLevel(result.getRiskLevel().byteValue());
        event.setStatus((byte) 1);
        alarmEventService.updateById(event);

        if (Boolean.TRUE.equals(result.getIsRealAlarm())) {
            AlarmDisposal disposal = new AlarmDisposal();
            disposal.setAlarmId(event.getAlarmId());
            disposal.setDisposalTime(LocalDateTime.now());
            disposal.setDisposalContent(
                    String.format("[AI自动研判]\n分析: %s\n建议处置: %s",
                            result.getAnalysisSummary(), result.getSuggestedAction())
            );
            alarmDisposalService.save(disposal);
            log.info("[AI研判] 告警确认属实 alarmId={}, 已自动生成处置记录 disposalId={}",
                    event.getAlarmId(), disposal.getDisposalId());
        } else {
            log.info("[AI研判] 告警判定为非真实事件 alarmId={}, 仅更新状态");
        }
    }
}
