package com.gazi.gazi_renew.admin.domain.dto;

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

    public static AiFilterReport from(AiFilterReportCreate aiFilterReportCreate) {
        return AiFilterReport.builder()
                .issueContent(aiFilterReportCreate.getIssueContent)
                .aiFiltered(aiFilterReportCreate.getAiFiltered)
                .registered(aiFilterReportCreate.getRegistered)
                .failureReason(aiFilterReportCreate.getFailureReason)
                .createdAt(aiFilterReportCreate.getCreatedAt)
                .build();
    }
}
