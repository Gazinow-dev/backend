package com.gazi.gazi_renew.admin.service;

import com.gazi.gazi_renew.admin.controller.port.AiFilterReportService;
import com.gazi.gazi_renew.admin.domain.dto.AiFilterReport;
import com.gazi.gazi_renew.admin.domain.dto.AiFilterReportCreate;
import com.gazi.gazi_renew.admin.service.port.AiFilterReportRepository;
import com.gazi.gazi_renew.common.service.port.ClockHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor
public class AiFilterReportServiceImpl implements AiFilterReportService {
    private final AiFilterReportRepository aiFilterReportRepository;
    private final ClockHolder clockHolder;
    @Override
    @Transactional
    public AiFilterReport save(AiFilterReportCreate aiFilterReportCreate) {
        return aiFilterReportRepository.save(AiFilterReport.from(aiFilterReportCreate, clockHolder));
    }
    @Override
    @Transactional(readOnly = true)
    public List<AiFilterReport> getThisWeekReport() {
        return aiFilterReportRepository.getThisWeekReport(clockHolder.now().minusDays(7), clockHolder.now());
    }
}
