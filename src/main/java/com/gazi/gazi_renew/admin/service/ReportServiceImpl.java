package com.gazi.gazi_renew.admin.service;

import com.gazi.gazi_renew.admin.controller.port.ReportService;
import com.gazi.gazi_renew.admin.domain.Penalty;
import com.gazi.gazi_renew.admin.domain.Report;
import com.gazi.gazi_renew.admin.domain.ReportStatus;
import com.gazi.gazi_renew.admin.domain.SanctionCriteria;
import com.gazi.gazi_renew.admin.domain.dto.ReportCreate;
import com.gazi.gazi_renew.admin.service.port.PenaltyRepository;
import com.gazi.gazi_renew.admin.service.port.ReportRepository;
import com.gazi.gazi_renew.common.controller.port.SecurityUtilService;
import com.gazi.gazi_renew.common.exception.ErrorCode;
import com.gazi.gazi_renew.common.service.port.ClockHolder;
import com.gazi.gazi_renew.issue.domain.IssueComment;
import com.gazi.gazi_renew.issue.service.port.IssueCommentRepository;
import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.service.port.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;
    private final SecurityUtilService securityUtilService;
    private final PenaltyRepository penaltyRepository;
    private final IssueCommentRepository issueCommentRepository;
    private final DiscordNotifier discordNotifier;
    private final ClockHolder clockHolder;
    @Override
    @Transactional
    public Report createReport(ReportCreate reportCreate) {
        Member reporterMember = memberRepository.findByEmail(securityUtilService.getCurrentUserEmail())
                .orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다"));
        Long reporterMemberId = reporterMember.getId(); //신고자
        IssueComment issueComment = issueCommentRepository.findByIssueCommentId(reportCreate.getReportedCommentId())
                .orElseThrow(() -> new EntityNotFoundException("해당 댓글이 존재하지 않습니다"));

        if (reportRepository.existsByIssueCommentIdAndReporterMemberId(issueComment.getIssueCommentId(), reporterMemberId)) {
            throw ErrorCode.throwDuplicateReportException();
        }
        //댓글 신고 횟수 1회 증가
        issueComment = issueComment.addReportedCount();
        issueCommentRepository.updateReportedCount(issueComment);

        Member reportedMember = memberRepository.findById(issueComment.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다")); //신고 대상자

        Report report = Report.create(reportCreate, reporterMemberId, reportedMember.getId(), clockHolder);
        report = reportRepository.save(report);
        discordNotifier.sendReportNotification(report, reporterMember, reportedMember, issueComment); //신고자,신고 대상자
        return report;
    }
    @Override
    @Transactional
    public Report approveReport(String sanctionCriteriaValue, Long reportId) {
        Report report = reportRepository.findByReportId(reportId)
                .orElseThrow(() -> new EntityNotFoundException("해당 신고를 찾을 수 없습니다"));
        if (report.getReportStatus() != ReportStatus.PENDING) {
            throw ErrorCode.throwDuplicateProcessReportException();
        }
        int reportCount = reportRepository.countByReportedMemberIdAndSanctionCriteria(report.getReportedMemberId(), SanctionCriteria.valueOf(sanctionCriteriaValue));
        // 제재 대상 결정 (허위 신고 여부 판단)
        Long penaltyTargetMemberId;
        if (sanctionCriteriaValue.equalsIgnoreCase("FALSE_REPORT")) {
            penaltyTargetMemberId = report.getReporterMemberId(); // 신고자 제재
        } else {
            penaltyTargetMemberId = report.getReportedMemberId(); // 신고 대상자 제재
        }

        Penalty penalty = penaltyRepository.findOrCreatePenalty(penaltyTargetMemberId);
        report = report.approveReport(sanctionCriteriaValue, penalty, reportCount + 1, clockHolder);

        deleteIssueComment(report);

        reportRepository.updateReportStatus(report);
        penaltyRepository.updatePenalty(penalty);

        return report;
    }
    @Override
    @Transactional
    public void rejectReport(Long reportId) {
        Report report = reportRepository.findByReportId(reportId)
                .orElseThrow(() -> new EntityNotFoundException("해당 신고를 찾을 수 없습니다"));
        IssueComment issueComment = issueCommentRepository.findByIssueCommentId(report.getIssueCommentId())
                .orElseThrow(() -> new EntityNotFoundException("해당 댓글을 찾을 수 없습니다"));

        report = report.rejectReport();
        issueComment = issueComment.decreaseReportedCount();

        issueCommentRepository.updateReportedCount(issueComment);
        reportRepository.updateReportStatus(report);
    }
    private void deleteIssueComment(Report report) {
        if (report.getIsDeletedComment() == Boolean.TRUE) {
            issueCommentRepository.deleteComment(report.getIssueCommentId());
        }
    }
}
