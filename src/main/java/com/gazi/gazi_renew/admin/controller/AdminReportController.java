package com.gazi.gazi_renew.admin.controller;

import com.gazi.gazi_renew.admin.controller.port.ReportService;
import com.gazi.gazi_renew.admin.domain.Report;
import com.gazi.gazi_renew.admin.service.port.ReportRepository;
import com.gazi.gazi_renew.issue.domain.IssueComment;
import com.gazi.gazi_renew.issue.service.port.IssueCommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
public class AdminReportController {
    private ReportService reportService;
    private final ReportRepository reportRepository;
    private final IssueCommentRepository issueCommentRepository;

    @GetMapping("/{reportId}")
    public String handleReportRequest(
            @PathVariable Long reportId,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam String reporter,
            Model model) {

        // 신고 정보 조회
        Report report = reportRepository.findByReportId(reportId)
                .orElseThrow(() -> new EntityNotFoundException("해당 신고를 찾을 수 없습니다"));

        IssueComment comment = issueCommentRepository.findByIssueCommentId(report.getIssueCommentId())
                .orElseThrow(() -> new EntityNotFoundException("해당 댓글을 찾을 수 없습니다"));

        // 모델에 데이터 추가
        model.addAttribute("title", title);
        model.addAttribute("content", content);
        model.addAttribute("reporter", reporter);
        model.addAttribute("report", report);
        model.addAttribute("comment", comment);

        return "admin/report-details"; // Thymeleaf 뷰로 전달
    }
    @PostMapping("/{reportId}/process")
    public String processReport(
            @PathVariable Long reportId,
            @RequestParam String sanctionCriteria, // 제재 기준
            @RequestParam String action,           // 적합/부적합
            Model model) {

        if ("APPROVED".equalsIgnoreCase(action)) {
            reportService.approveReport(sanctionCriteria, reportId); // 적합 처리
        } else {
            reportService.rejectReport(reportId); // 부적합 처리
        }

        return "redirect:/admin/reports"; // 처리 후 메인 페이지로 리다이렉트
    }

}
