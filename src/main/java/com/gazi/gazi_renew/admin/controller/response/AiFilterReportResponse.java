package com.gazi.gazi_renew.admin.controller.response;

import com.gazi.gazi_renew.admin.domain.dto.AiFilterReport;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class AiFilterReportResponse {
    private final Long id;
    private final String issueTitle;
    private final String issueContent;
    private final boolean aiFiltered;
    private final boolean registered;
    private final String failureReason;
    private final LocalDateTime createdAt;
    @Builder
    public AiFilterReportResponse(Long id, String issueTitle, String issueContent, boolean aiFiltered, boolean registered, String failureReason, LocalDateTime createdAt) {
        this.id = id;
        this.issueTitle = issueTitle;
        this.issueContent = issueContent;
        this.aiFiltered = aiFiltered;
        this.registered = registered;
        this.failureReason = failureReason;
        this.createdAt = createdAt;
    }
    public static List<AiFilterReportResponse> fromList(List<AiFilterReport> aiFilterReports) {
        return aiFilterReports.stream()
                .map(aiFilterReport -> {
                    return AiFilterReportResponse.builder()
                            .id(aiFilterReport.getId())
                            .issueTitle(aiFilterReport.getIssueTitle())
                            .issueContent(aiFilterReport.getIssueContent())
                            .aiFiltered(aiFilterReport.isAiFiltered())
                            .registered(aiFilterReport.isRegistered())
                            .failureReason(aiFilterReport.getFailureReason())
                            .createdAt(aiFilterReport.getCreatedAt())
                            .build();
                }).collect(Collectors.toList());
    }
    public static AiFilterReportResponse from(AiFilterReport aiFilterReport) {
        return AiFilterReportResponse.builder()
                .id(aiFilterReport.getId())
                .issueTitle(aiFilterReport.getIssueTitle())
                .issueContent(aiFilterReport.getIssueContent())
                .aiFiltered(aiFilterReport.isAiFiltered())
                .registered(aiFilterReport.isRegistered())
                .failureReason(aiFilterReport.getFailureReason())
                .createdAt(aiFilterReport.getCreatedAt())
                .build();
    }
}
