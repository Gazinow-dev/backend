package com.gazi.gazi_renew.admin.infrastructure.entity;

import com.gazi.gazi_renew.admin.domain.dto.AiFilterReport;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AiFilterReportEntity {
    @Id
    private Long id;
    private String issueTitle;
    @Column(columnDefinition = "TEXT")
    private String issueContent;
    private boolean aiFiltered;
    private boolean registered;
    private String failureReason;
    private LocalDateTime createdAt;

    public static AiFilterReportEntity from(AiFilterReport aiFilterReport) {
        AiFilterReportEntity aiFilterReportEntity = new AiFilterReportEntity();
        aiFilterReportEntity.issueTitle = aiFilterReport.getIssueTitle();
        aiFilterReportEntity.issueContent = aiFilterReport.getIssueContent();
        aiFilterReportEntity.aiFiltered = aiFilterReport.isAiFiltered();
        aiFilterReportEntity.registered = aiFilterReport.isRegistered();
        aiFilterReportEntity.failureReason = aiFilterReport.getFailureReason();
        aiFilterReportEntity.createdAt = aiFilterReport.getCreatedAt();

        return aiFilterReportEntity;
    }
    public AiFilterReport toModel() {
        return AiFilterReport.builder()
                .id(this.id)
                .issueTitle(this.issueTitle)
                .issueContent(this.issueContent)
                .aiFiltered(this.aiFiltered)
                .registered(this.registered)
                .failureReason(this.failureReason)
                .createdAt(this.createdAt)
                .build();
    }
}
