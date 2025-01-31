package com.gazi.gazi_renew.admin.controller.port;

import com.gazi.gazi_renew.admin.domain.dto.AiFilterReport;

import java.util.List;

public interface AiFilterReportService {
    public AiFilterReport save(AiFilterReportCreate aiFilterReportCreate);

    public List<AiFilterReport> getThisWeekReport();
}
