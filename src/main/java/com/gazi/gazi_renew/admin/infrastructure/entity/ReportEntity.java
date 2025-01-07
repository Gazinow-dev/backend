package com.gazi.gazi_renew.admin.infrastructure.entity;

import com.gazi.gazi_renew.admin.domain.Report;
import com.gazi.gazi_renew.admin.domain.ReportReason;
import com.gazi.gazi_renew.admin.domain.ReportStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "report")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportEntity {
    @Id
    @Column(name = "report_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long reportedMemberId;
    private Long reporterMemberId;
    private Long issueCommentId;
    @Enumerated(EnumType.STRING)
    private ReportReason reportReason;
    private String reasonDescription;
    @Enumerated(EnumType.STRING)
    private ReportStatus reportStatus;
    private LocalDateTime reportedAt;

    public static ReportEntity from(Report report) {
        ReportEntity reportEntity = new ReportEntity();
        reportEntity.id = report.getReportId();
        reportEntity.reportedMemberId = report.getReportedMemberId();
        reportEntity.reporterMemberId = report.getReporterMemberId();
        reportEntity.issueCommentId = report.getIssueCommentId();
        reportEntity.reportReason = report.getReportReason();
        reportEntity.reasonDescription = report.getReasonDescription();
        reportEntity.reportStatus = report.getReportStatus();
        reportEntity.reportedAt = report.getReportedAt();

        return reportEntity;
    }
    public Report toModel() {
        return Report.builder()
                .reportId(id)
                .reporterMemberId(reporterMemberId)
                .reportedMemberId(reportedMemberId)
                .issueCommentId(issueCommentId)
                .reportReason(reportReason)
                .reasonDescription(reasonDescription)
                .reportStatus(reportStatus)
                .reportedAt(reportedAt)
                .build();
    }
}
