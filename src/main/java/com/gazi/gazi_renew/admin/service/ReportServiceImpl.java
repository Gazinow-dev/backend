package com.gazi.gazi_renew.admin.service;

import com.gazi.gazi_renew.admin.controller.port.ReportService;
import com.gazi.gazi_renew.admin.domain.Penalty;
import com.gazi.gazi_renew.admin.domain.Report;
import com.gazi.gazi_renew.admin.domain.dto.ReportCreate;
import com.gazi.gazi_renew.admin.service.port.PenaltyRepository;
import com.gazi.gazi_renew.admin.service.port.ReportRepository;
import com.gazi.gazi_renew.common.controller.port.SecurityUtilService;
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
    private final ClockHolder clockHolder;
    @Override
    @Transactional
    public Report createReport(ReportCreate reportCreate) {
        Member member = memberRepository.findByEmail(securityUtilService.getCurrentUserEmail())
                .orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다"));
        Long reportedId = member.getId(); //신고자
        IssueComment issueComment = issueCommentRepository.findByIssueCommentId(reportCreate.getReportedCommentId())
                .orElseThrow(() -> new EntityNotFoundException("해당 댓글이 존재하지 않습니다"));
        Long reporterId = issueComment.getMemberId(); //신고 대상자
        //TODO : 디스 코드 웹훅으로 신고 보내기 ( 서비스 따로 만들고 신고 대상자 , 신고자 닉네임 추가, 도메인에 별표 참고)
        return reportRepository.save(Report.create(reportCreate, reportedId, reporterId, clockHolder));
    }
    @Override
    @Transactional
    public void approveReport(String sanctionCriteriaValue, Long reportId) {
        Report report = reportRepository.findByReportId(reportId)
                .orElseThrow(() -> new EntityNotFoundException("해당 신고를 찾을 수 없습니다"));
        int reportCount = reportRepository.countByReportedMemberId(report.getReportedMemberId());

        Penalty penalty = penaltyRepository.findOrCreatePenalty(report.getReportedMemberId());
        report = report.approveReport(sanctionCriteriaValue, penalty, reportCount, clockHolder);

        reportRepository.updateReportStatus(report);
        penaltyRepository.updatePenalty(penalty);
    }
    @Override
    @Transactional
    public void rejectReport(Long reportId) {
        Report report = reportRepository.findByReportId(reportId)
                .orElseThrow(() -> new EntityNotFoundException("해당 신고를 찾을 수 없습니다"));
        report = report.rejectReport();

        reportRepository.updateReportStatus(report);
    }
}
