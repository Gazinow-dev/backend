package com.gazi.gazi_renew.admin.controller.port;

import com.gazi.gazi_renew.admin.domain.Report;
import com.gazi.gazi_renew.admin.domain.dto.ReportCreate;

public interface ReportService {
    void createReport(ReportCreate reportCreate);

    void approveReport(String sanctionCriteriaValue, Long reportId);
    void rejectReport(Long reportId);
}
