package com.gazi.gazi_renew.admin.infrastructure;

import com.gazi.gazi_renew.admin.domain.dto.AiFilterReport;
import com.gazi.gazi_renew.admin.infrastructure.entity.AiFilterReportEntity;
import com.gazi.gazi_renew.admin.infrastructure.jpa.AiFilterReportJpaRepositoryImpl;
import com.gazi.gazi_renew.admin.service.port.AiFilterReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AiFilterReportRepositoryImpl implements AiFilterReportRepository {
    private final AiFilterReportJpaRepositoryImpl aiFilterReportJpaRepository;
    @Override
    public AiFilterReport save(AiFilterReport aiFilterReport) {
        return aiFilterReportJpaRepository.save(AiFilterReportEntity.from(aiFilterReport)).toModel();
    }

    @Override
    public List<AiFilterReport> getThisWeekReport(LocalDateTime startDate , LocalDateTime endDate) {
        return aiFilterReportJpaRepository.getThisWeekReport(startDate, endDate)
                .stream().map(AiFilterReportEntity::toModel).collect(Collectors.toList());
    }
}
