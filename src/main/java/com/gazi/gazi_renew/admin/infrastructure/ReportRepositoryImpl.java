package com.gazi.gazi_renew.admin.infrastructure;

import com.gazi.gazi_renew.admin.domain.Report;
import com.gazi.gazi_renew.admin.domain.SanctionCriteria;
import com.gazi.gazi_renew.admin.infrastructure.entity.ReportEntity;
import com.gazi.gazi_renew.admin.infrastructure.jpa.ReportJpaRepository;
import com.gazi.gazi_renew.admin.service.port.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReportRepositoryImpl implements ReportRepository {
    private final ReportJpaRepository reportJpaRepository;
    @Override
    public Report save(Report report) {
        return reportJpaRepository.save(ReportEntity.from(report)).toModel();
    }
    @Override
    public Optional<Report> findByReportId(Long reportId) {
        return reportJpaRepository.findById(reportId).map(ReportEntity::toModel);
    }
    @Override
    public void updateReportStatus(Report report) {
        reportJpaRepository.updateReportStatus(report.getReportId(), report.getReportStatus(), report.getSanctionCriteria());
    }
    @Override
    public int countByReportedMemberIdAndSanctionCriteria(Long reportedMemberId, SanctionCriteria sanctionCriteria) {
        return reportJpaRepository.countByReportedMemberIdAndSanctionCriteria(reportedMemberId, sanctionCriteria).intValue();
    }
    @Override
    public boolean existsByIssueCommentIdAndReporterMemberId(Long issueCommentId, Long reporterMemberId) {
        return reportJpaRepository.existsByIssueCommentIdAndReportedMemberId(issueCommentId, reporterMemberId);
    }
}
