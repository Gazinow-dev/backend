package com.gazi.gazi_renew.admin.service.port;

import com.gazi.gazi_renew.admin.domain.Report;
import com.gazi.gazi_renew.admin.domain.SanctionCriteria;

import java.util.Optional;

public interface ReportRepository {
    Report save(Report report);

    Optional<Report> findByReportId(Long reportId);
    void updateReportStatus(Report report);

    int countByReportedMemberIdAndSanctionCriteria(Long reportedMemberId, SanctionCriteria sanctionCriteriaValue);

    boolean existsByIssueCommentIdAndReporterMemberId(Long issueCommentId, Long reporterMemberId);
}
