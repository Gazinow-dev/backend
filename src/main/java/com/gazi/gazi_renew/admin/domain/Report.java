package com.gazi.gazi_renew.admin.domain;

import com.gazi.gazi_renew.admin.domain.dto.ReportCreate;
import com.gazi.gazi_renew.common.service.port.ClockHolder;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Report {
    private final Long reportId;
    private final Long reportedMemberId;
    private final Long reporterMemberId;
    private final Long issueCommentId;
    private final ReportReason reportReason;
    private final String reasonDescription;
    private final LocalDateTime reportedAt;
    private final ReportStatus reportStatus;
    private final Boolean isDeletedComment;
    private final SanctionCriteria sanctionCriteria;
    @Builder
    public Report(Long reportId, Long reportedMemberId, Long reporterMemberId, Long issueCommentId, ReportReason reportReason, String reasonDescription, LocalDateTime reportedAt, ReportStatus reportStatus, Boolean isDeletedComment, SanctionCriteria sanctionCriteria) {
        this.reportId = reportId;
        this.reportedMemberId = reportedMemberId;
        this.reporterMemberId = reporterMemberId;
        this.issueCommentId = issueCommentId;
        this.reportReason = reportReason;
        this.reasonDescription = reasonDescription;
        this.reportedAt = reportedAt;
        this.reportStatus = reportStatus;
        this.isDeletedComment = isDeletedComment;
        this.sanctionCriteria = sanctionCriteria;
    }

    public static Report create(ReportCreate reportCreate, Long reporterMemberId, Long reportedMemberId, ClockHolder clockHolder) {
        return Report.builder()
                .reporterMemberId(reporterMemberId)
                .reportedMemberId(reportedMemberId)
                .issueCommentId(reportCreate.getReportedCommentId())
                .reportReason(ReportReason.valueOf(reportCreate.getReason()))
                .reasonDescription(reportCreate.getReasonDescription())
                .reportedAt(clockHolder.now())
                .reportStatus(ReportStatus.PENDING)
                .build();
    }
    //승인 프로세스 관리자 페이지에서 제제기준 값이 넘어올거임
    // reportCount는 현재 신고횟수+1 을 넘겨줘야함
    public Report approveReport(String sanctionCriteriaValue, Penalty penalty, int reportCount, ClockHolder clockHolder) {
        // 제재 기준에 따른 Penalty 연장 처리
        if (sanctionCriteriaValue.equalsIgnoreCase("ADVERTISEMENT")) {
            // 광고 및 홍보: 댓글 삭제 + 1년 댓글 작성 제한
            penalty.extendPenalty(365, clockHolder);
            return getApproveReport(Boolean.TRUE, SanctionCriteria.valueOf(sanctionCriteriaValue));
        } else if (sanctionCriteriaValue.equalsIgnoreCase("OTHER_VIOLATIONS")) {
            // 광고 외 위반: 신고 횟수별 기간 연장
            int penaltyDays = calculatePenaltyDays(reportCount);
            penalty.extendPenalty(penaltyDays, clockHolder);

            return getApproveReport(Boolean.TRUE, SanctionCriteria.valueOf(sanctionCriteriaValue));
        } else if (sanctionCriteriaValue.equalsIgnoreCase("FALSE_REPORT")) {
            // 허위 신고: 신고 횟수별 기간 연장
            int penaltyDays = calculateFalseReportPenaltyDays(reportCount);
            penalty.extendPenalty(penaltyDays, clockHolder);
            return getApproveReport(Boolean.FALSE, SanctionCriteria.valueOf(sanctionCriteriaValue));
        } else {
            // 유효하지 않은 제재 기준 값에 대한 처리
            throw new IllegalArgumentException("유효하지 않은 제제 기준 입니다 : " + sanctionCriteriaValue);
        }
        // 신고 상태를 APPROVED로 변경
    }
    public Report rejectReport() {
        return Report.builder()
                .reportId(reportId)
                .reporterMemberId(reporterMemberId)
                .reportedMemberId(reportedMemberId)
                .issueCommentId(issueCommentId)
                .reportReason(reportReason)
                .reasonDescription(reasonDescription)
                .reportedAt(reportedAt)
                .reportStatus(ReportStatus.REJECTED)
                .build();
    }
    private int calculatePenaltyDays(int reportCount) {
        // 광고 외의 위반: 신고 횟수별 제재 기간
        if (reportCount == 1) {
            return 1;
        } else if (reportCount == 2) {
            return 3;
        } else if (reportCount == 3) {
            return 7;
        } else if (reportCount == 4) {
            return 14;
        } else {
            return 30; // 5회 이상
        }
    }
    private int calculateFalseReportPenaltyDays(int reportCount) {
        // 허위 신고: 신고 횟수별 제재 기간
        if (reportCount == 1) {
            return 7;
        } else if (reportCount == 2) {
            return 14;
        } else if (reportCount == 3) {
            return 30;
        } else if (reportCount == 4) {
            return 50;
        } else {
            return 90; // 5회 이상
        }
    }
    private Report getApproveReport(Boolean isDeletedComment, SanctionCriteria sanctionCriteriaValue) {
        return Report.builder()
                .reportId(reportId)
                .reporterMemberId(reporterMemberId)
                .reportedMemberId(reportedMemberId)
                .issueCommentId(issueCommentId)
                .reasonDescription(reasonDescription)
                .reportedAt(reportedAt)
                .reportStatus(ReportStatus.APPROVED)
                .isDeletedComment(isDeletedComment)
                .sanctionCriteria(sanctionCriteriaValue)
                .build();
    }

}
