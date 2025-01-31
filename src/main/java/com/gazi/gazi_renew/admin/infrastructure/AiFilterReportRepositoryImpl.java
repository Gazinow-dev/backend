package com.gazi.gazi_renew.admin.infrastructure;

import com.gazi.gazi_renew.admin.domain.dto.AiFilterReport;
import com.gazi.gazi_renew.admin.service.port.AiFilterReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
@Repository
@RequiredArgsConstructor
public class AiFilterReportRepositoryImpl implements AiFilterReportRepository {
    private final AiFilterReportJpaRepositoryImpl aiFilterReportJpaRepository;
    @Override
    public AiFilterReport save(AiFilterReport aiFilterReport) {
        return aiFilterReportJpaRepository.save(AifilterReportEntity.from(aiFilterReport));
    }

    @Override
    public List<AiFilterReport> getThisWeekReport(LocalDateTime now) {
        return aiFilterReportJpaRepository.getThisWeekReport();
    }
}
