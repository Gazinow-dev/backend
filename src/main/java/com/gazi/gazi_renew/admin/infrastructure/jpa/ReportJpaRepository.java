package com.gazi.gazi_renew.admin.infrastructure.jpa;


import com.gazi.gazi_renew.admin.domain.ReportStatus;
import com.gazi.gazi_renew.admin.domain.SanctionCriteria;
import com.gazi.gazi_renew.admin.infrastructure.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReportJpaRepository extends JpaRepository<ReportEntity , Long> {

    @Modifying
    @Query("UPDATE ReportEntity r SET r.reportStatus= :reportStatus, r.sanctionCriteria= :sanctionCriteria WHERE r.id = :id")
    void updateReportStatus(@Param("id") Long id, @Param("reportStatus") ReportStatus reportStatus, @Param("sanctionCriteria") SanctionCriteria sanctionCriteria);

    Long countByReportedMemberIdAndSanctionCriteria(Long reportedMemberId, SanctionCriteria sanctionCriteria);

    boolean existsByIssueCommentIdAndReporterMemberId(Long issueCommentId, Long reporterMemberId);
}
