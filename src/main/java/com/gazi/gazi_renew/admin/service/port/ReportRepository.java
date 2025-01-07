package com.gazi.gazi_renew.admin.service.port;

import com.gazi.gazi_renew.admin.domain.Report;

import java.util.Optional;

public interface ReportRepository {
    Report save(Report report);

    Optional<Report> findByReportId(Long reportId);
    int countByReportedMemberId(Long reportedMemberId);

    void updateReportStatus(Report report);

}
