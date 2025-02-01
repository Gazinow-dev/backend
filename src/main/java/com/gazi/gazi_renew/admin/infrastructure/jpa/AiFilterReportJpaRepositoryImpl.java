package com.gazi.gazi_renew.admin.infrastructure.jpa;

import com.gazi.gazi_renew.admin.domain.dto.AiFilterReport;
import com.gazi.gazi_renew.admin.infrastructure.entity.AiFilterReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AiFilterReportJpaRepositoryImpl extends JpaRepository<AiFilterReportEntity, Long> {
    @Query("SELECT a FROM AiFilterReportEntity a WHERE a.createdAt BETWEEN :startDate AND :endDate")
    List<AiFilterReportEntity> getThisWeekReport(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
