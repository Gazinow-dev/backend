package com.gazi.gazi_renew.admin.domain.dto;

import com.gazi.gazi_renew.common.service.port.ClockHolder;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
@Getter
public class AiFilterReport {
    private final Long id;
    private final String issueContent;
    private final boolean aiFiltered;
    private final boolean registered;
    private final String failureReason;
    private final LocalDateTime createdAt;
    @Builder
    public AiFilterReport(Long id, String issueContent, boolean aiFiltered, boolean registered, String failureReason, LocalDateTime createdAt) {
        this.id = id;
        this.issueContent = issueContent;
        this.aiFiltered = aiFiltered;
        this.registered = registered;
        this.failureReason = failureReason;
        this.createdAt = createdAt;
    }
    public static AiFilterReport from(AiFilterReportCreate aiFilterReportCreate, ClockHolder clockHolder) {
        return AiFilterReport.builder()
                .issueContent(aiFilterReportCreate.getIssueContent())
                .aiFiltered(aiFilterReportCreate.isAiFiltered())
                .registered(aiFilterReportCreate.isRegistered())
                .failureReason(aiFilterReportCreate.getFailureReason())
                .createdAt(clockHolder.now())
                .build();
    }
}
