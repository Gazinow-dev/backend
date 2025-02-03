package com.gazi.gazi_renew.admin.controller;

import com.gazi.gazi_renew.admin.controller.port.AiFilterReportService;
import com.gazi.gazi_renew.admin.controller.response.AiFilterReportResponse;
import com.gazi.gazi_renew.admin.domain.dto.AiFilterReport;
import com.gazi.gazi_renew.admin.domain.dto.AiFilterReportCreate;
import com.gazi.gazi_renew.common.controller.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/aiFilterReport")
@RequiredArgsConstructor
public class AiFilterReportController {
    private final Response response;
    private final AiFilterReportService aiFilterReportService;

    @GetMapping("")
    public ResponseEntity<Response.Body> getAiFilterReports() {
        List<AiFilterReport> thisWeekReport = aiFilterReportService.getThisWeekReport();
        return response.success(AiFilterReportResponse.fromList(thisWeekReport), "금주 topis 이슈 리포트 조회 완료", HttpStatus.OK);
    }
    @PostMapping("")
    public ResponseEntity<Response.Body> saveAiFilterReport(@RequestBody AiFilterReportCreate aiFilterReportCreate) {
        AiFilterReport aiFilterReport = aiFilterReportService.save(aiFilterReportCreate);
        return response.success(AiFilterReportResponse.from(aiFilterReport), "topis 이슈 현환 저장 완료", HttpStatus.OK);
    }
}
