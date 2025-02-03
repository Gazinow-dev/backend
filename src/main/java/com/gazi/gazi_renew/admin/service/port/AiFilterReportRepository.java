package com.gazi.gazi_renew.admin.service.port;

import com.gazi.gazi_renew.admin.domain.dto.AiFilterReport;

import java.time.LocalDateTime;
import java.util.List;

public interface AiFilterReportRepository {
    AiFilterReport save(AiFilterReport aiFilterReport);

    List<AiFilterReport> getThisWeekReport(LocalDateTime startDate , LocalDateTime endDate);
}
