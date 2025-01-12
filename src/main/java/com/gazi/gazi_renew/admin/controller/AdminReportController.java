package com.gazi.gazi_renew.admin.controller;

import com.gazi.gazi_renew.admin.controller.port.ReportService;
import com.gazi.gazi_renew.admin.domain.Report;
import com.gazi.gazi_renew.admin.domain.ReportStatus;
import com.gazi.gazi_renew.admin.service.port.ReportRepository;
import com.gazi.gazi_renew.common.controller.response.Response;
import com.gazi.gazi_renew.common.exception.ErrorCode;
import com.gazi.gazi_renew.issue.domain.IssueComment;
import com.gazi.gazi_renew.issue.service.port.IssueCommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
public class AdminReportController {
    private final ReportService reportService;
    private final ReportRepository reportRepository;
    private final IssueCommentRepository issueCommentRepository;
    private final Response response;
    @GetMapping("/{reportId}")
    public String handleReportRequest(
            @PathVariable Long reportId,
            @RequestParam String reportedAt,
            @RequestParam String reporterNickname,
            @RequestParam String reportReason,
            @RequestParam String reasonDescription,
            @RequestParam String commentCreatedAt,
            @RequestParam String commentContent,
            @RequestParam String reportedNickname,
            Model model) {

        // 신고 정보 조회
        Report report = reportRepository.findByReportId(reportId)
                .orElseThrow(() -> new EntityNotFoundException("해당 신고를 찾을 수 없습니다"));
        if (report.getReportStatus() != ReportStatus.PENDING) {
            throw ErrorCode.throwDuplicateProcessReportException();
        }
        IssueComment comment = issueCommentRepository.findByIssueCommentId(report.getIssueCommentId())
                .orElseThrow(() -> new EntityNotFoundException("해당 댓글을 찾을 수 없습니다"));

        // 모델에 데이터 추가
        model.addAttribute("report", report);
        model.addAttribute("comment", comment);
        model.addAttribute("reportedAt", reportedAt);
        model.addAttribute("reporterNickname", reporterNickname);
        model.addAttribute("reportReason", reportReason);
        model.addAttribute("reasonDescription", reasonDescription);
        model.addAttribute("commentCreatedAt", commentCreatedAt);
        model.addAttribute("commentContent", commentContent);
        model.addAttribute("reportedNickname", reportedNickname);

        return "admin/report-details"; // Thymeleaf 뷰로 전달
    }

    @PostMapping("/{reportId}/process")
    public ResponseEntity<Response.Body> processReport(
            @PathVariable Long reportId,
            @RequestParam String sanctionCriteria, // 제재 기준
            @RequestParam String action) {         // 적합/부적합

        if ("APPROVED".equalsIgnoreCase(action)) {
            reportService.approveReport(sanctionCriteria, reportId); // 적합 처리
        } else {
            reportService.rejectReport(reportId); // 부적합 처리
        }
        // 처리 후 목록 페이지로 리다이렉트
        return response.success("제제 적합/부적합 판단이 완료됐습니다.");
    }
}
