package com.gazi.gazi_renew.admin.service;

import com.gazi.gazi_renew.admin.domain.Report;
import com.gazi.gazi_renew.admin.domain.ReportReason;
import com.gazi.gazi_renew.admin.domain.ReportStatus;
import com.gazi.gazi_renew.admin.domain.SanctionCriteria;
import com.gazi.gazi_renew.admin.domain.dto.ReportCreate;
import com.gazi.gazi_renew.common.exception.CustomException;
import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.domain.IssueComment;
import com.gazi.gazi_renew.issue.domain.enums.IssueKeyword;
import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.domain.enums.Role;
import com.gazi.gazi_renew.mock.admin.FakePenaltyRepository;
import com.gazi.gazi_renew.mock.admin.FakeReportRepository;
import com.gazi.gazi_renew.mock.common.TestClockHolder;
import com.gazi.gazi_renew.mock.issue.FakeIssueCommentRepository;
import com.gazi.gazi_renew.mock.member.FakeMemberRepository;
import com.gazi.gazi_renew.mock.common.FakeSecurityUtil;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

class ReportServiceImplTest {
    private ReportServiceImpl reportServiceImpl;
    private FakeIssueCommentRepository fakeIssueCommentRepository;
    @Mock
    DiscordNotifier notifier;
    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);

        FakeReportRepository fakeReportRepository = new FakeReportRepository();
        FakeSecurityUtil fakeSecurityUtil = new FakeSecurityUtil();
        FakeMemberRepository fakeMemberRepository = new FakeMemberRepository();
        FakePenaltyRepository fakePenaltyRepository = new FakePenaltyRepository();
        fakeIssueCommentRepository = new FakeIssueCommentRepository();

        LocalDateTime newTime = LocalDateTime.now();
        TestClockHolder testClockHolder = new TestClockHolder(newTime);


        reportServiceImpl = new ReportServiceImpl(fakeReportRepository, fakeMemberRepository,
                fakeSecurityUtil, fakePenaltyRepository, fakeIssueCommentRepository, notifier, testClockHolder);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Member member1 = Member.builder()
                .id(1L)
                .email("mw310@naver.com")
                .password("encoded_tempPassword")
                .nickName("minu")
                .role(Role.ROLE_USER)
                .pushNotificationEnabled(false)
                .mySavedRouteNotificationEnabled(false)
                .firebaseToken("firebaseToken")
                .build();
        fakeMemberRepository.save(member1);
        fakeSecurityUtil.addEmail("mw310@naver.com");

        Issue issue = Issue.builder()
                .id(1L)
                .title("삼각지역 집회")
                .content("삼각지역 집회 가는길 지금 이슈 테스트")
                .startDate(LocalDateTime.parse("2024-11-15 08:29:00", formatter))
                .expireDate(LocalDateTime.parse("2024-11-15 10:29:00", formatter))
                .keyword(IssueKeyword.시위)
                .crawlingNo("1")
                .likeCount(10)
                .build();
        
        IssueComment issueComment = IssueComment.builder()
                .issueCommentId(1L)
                .issue(issue)
                .memberId(1L)
                .issueCommentContent("가는길 지금 이슈 댓글 테스트입니다")
                .createdBy("이민우")
                .createdAt(LocalDateTime.now())
                .reportedCount(1)
                .build();

        IssueComment issueComment2 = IssueComment.builder()
                .issueCommentId(2L)
                .issue(issue)
                .memberId(1L)
                .issueCommentContent("댓글!")
                .createdBy("이민우")
                .createdAt(LocalDateTime.now())
                .build();
        fakeIssueCommentRepository.saveComment(issueComment);
        fakeIssueCommentRepository.saveComment(issueComment2);
        
        Report report = Report.builder()
                .reporterMemberId(1L)
                .reportedMemberId(2L)
                .issueCommentId(1L)
                .reportReason(ReportReason.INAPPROPRIATE_CONTENT)
                .reasonDescription("광고성 댓글입니다!!")
                .reportedAt(newTime)
                .reportStatus(ReportStatus.PENDING)
                .sanctionCriteria(SanctionCriteria.NONE)
                .build();

        fakeReportRepository.save(report);
    }
    @Test
    void 신고를_하면_신고_상태가_대기중_이어야_한다() throws Exception{
        //given
        ReportCreate reportCreate = ReportCreate.builder()
                .reportedCommentId(2L)
                .reason("INAPPROPRIATE_CONTENT")
                .reasonDescription("욕설이 너무 심해요...")
                .build();
        doNothing().when(notifier).sendReportNotification(any(), any(), any(), any());
        //when
        Report report = reportServiceImpl.createReport(reportCreate);
        //then
        assertThat(report.getReportStatus()).isEqualTo(ReportStatus.PENDING);
        assertThat(report.getReportReason()).isEqualTo(ReportReason.INAPPROPRIATE_CONTENT);
        assertThat(report.getReasonDescription()).isEqualTo("욕설이 너무 심해요...");
    }
    @Test
    void 신고를_하면_신고한_댓글의_신고횟수가_증가한다() throws Exception{
        //given
        ReportCreate reportCreate = ReportCreate.builder()
                .reportedCommentId(2L)
                .reason("INAPPROPRIATE_CONTENT")
                .reasonDescription("욕설이 너무 심해요...")
                .build();
        doNothing().when(notifier).sendReportNotification(any(), any(), any(), any());
        //when
        Report report = reportServiceImpl.createReport(reportCreate);
        Optional<IssueComment> issueComment = fakeIssueCommentRepository.findByIssueCommentId(2L);

        //then
        assertThat(issueComment.get().getReportedCount()).isEqualTo(1);
    }
    @Test
    void 댓글_신고_제제_승인이_되면_해당_댓글은_삭제된다() throws Exception{
        //given
        String sanctionCriteriaValue = "ADVERTISEMENT";
        Long reportId = 1L;
        //when
        reportServiceImpl.approveReport(sanctionCriteriaValue, reportId);
        Optional<IssueComment> issueComment = fakeIssueCommentRepository.findByIssueCommentId(1L);
        //then
        assertThat(issueComment.isEmpty()).isTrue();
    }
    @Test
    void 사용자는_중복해서_댓글을_신고할_수_없다() throws Exception{
        //given
        String sanctionCriteriaValue = "ADVERTISEMENT";
        Long reportId = 1L;
        //when
        reportServiceImpl.approveReport(sanctionCriteriaValue, reportId);
        //then
        assertThatThrownBy(() -> reportServiceImpl.approveReport(sanctionCriteriaValue, reportId))
                .isInstanceOf(CustomException.class);
    }
    @Test
    void 허위_신고일_경우에는_신고자가_제제를_받는다() throws Exception{
        //given
        String sanctionCriteriaValue = "FALSE_REPORT";
        Long reportId = 1L;
        //when
        Report report = reportServiceImpl.approveReport(sanctionCriteriaValue, reportId);
        //then
        assertThat(report.getReportedMemberId()).isEqualTo(2L);
    }
    @Test
    void 신고를_제제_부적합_처리하면_해당_댓글의_신고_횟수는_감소해야한다() throws Exception{
        //given
        Long reportId = 1L;
        //when
        reportServiceImpl.rejectReport(reportId);
        IssueComment issueComment = fakeIssueCommentRepository.findByIssueCommentId(1L)
                .orElseThrow(() -> new EntityNotFoundException("해당 댓글이 존재하지 않습니다"));
        //then
        assertThat(issueComment.getReportedCount()).isEqualTo(0);
    }
}