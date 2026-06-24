package com.smartcampus.dto;

import lombok.Data;

@Data
public class AiAnalysisResponse {

    /** 研判后的风险等级 (1:紧急 2:重要 3:一般) */
    private Integer riskLevel;

    /** 是否为真实告警 */
    private Boolean isRealAlarm;

    /** AI 分析摘要 */
    private String analysisSummary;

    /** AI 建议的处置措施 */
    private String suggestedAction;
}
