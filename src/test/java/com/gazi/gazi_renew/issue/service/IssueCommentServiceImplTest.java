package com.gazi.gazi_renew.issue.service;

import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.domain.IssueComment;
import com.gazi.gazi_renew.issue.domain.MyCommentSummary;
import com.gazi.gazi_renew.issue.domain.dto.IssueCommentCreate;
import com.gazi.gazi_renew.issue.domain.dto.IssueCommentUpdate;
import com.gazi.gazi_renew.issue.domain.enums.IssueKeyword;
import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.domain.enums.Role;
import com.gazi.gazi_renew.mock.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class IssueCommentServiceImplTest {
    private IssueCommentServiceImpl issueCommentServiceImpl;
    @BeforeEach
    void init() {
        FakeIssueCommentRepository fakeIssueCommentRepository = new FakeIssueCommentRepository();
        FakeMemberRepository fakeMemberRepository = new FakeMemberRepository();
        FakeIssueRepository fakeIssueRepository = new FakeIssueRepository();
        LocalDateTime newTime = LocalDateTime.now();
        TestClockHolder testClockHolder = new TestClockHolder(newTime);
        FakeSecurityUtil fakeSecurityUtil = new FakeSecurityUtil();

        this.issueCommentServiceImpl = new IssueCommentServiceImpl(fakeIssueCommentRepository, fakeMemberRepository
                , testClockHolder, fakeSecurityUtil, fakeIssueRepository);
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
        fakeIssueRepository.save(issue);

        IssueComment issueComment = IssueComment.builder()
                .issueCommentId(1L)
                .issue(issue)
                .memberId(1L)
                .issueCommentContent("가는길 지금 이슈 댓글 테스트입니다")
                .createdBy("이민우")
                .createdAt(newTime)
                .build();

        IssueComment issueComment2 = IssueComment.builder()
                .issueCommentId(2L)
                .issue(issue)
                .memberId(1L)
                .issueCommentContent("두번쨰 가는길 지금 이슈 댓글 테스트입니다")
                .createdBy("이민우")
                .createdAt(newTime.plusMinutes(1))
                .build();

        fakeSecurityUtil.addEmail("mw310@naver.com");
        fakeMemberRepository.save(member1);

        fakeIssueCommentRepository.saveComment(issueComment);
        fakeIssueCommentRepository.saveComment(issueComment2);
    }
    @Test
    void IssueComment를_저장할_수_있다() throws Exception{
        //given
        IssueCommentCreate issueComment = IssueCommentCreate.builder()
                .issueId(1L)
                .issueCommentContent("새로운 가는길 지금 이슈 댓글 테스트입니다")
                .build();
        //when
        IssueComment result = issueCommentServiceImpl.saveComment(issueComment);
        //then
        assertThat(result.getMemberId()).isEqualTo(1L);
        assertThat(result.getIssueCommentContent()).isEqualTo("새로운 가는길 지금 이슈 댓글 테스트입니다");
    }
    @Test
    void 나의_정보에서_내가_쓴_댓글은_댓글을_단_시간_최신순으로_정렬된다() throws Exception{
        //when
        List<MyCommentSummary> issueComments = issueCommentServiceImpl.getIssueCommentsByMemberId();
        //then
        assertThat(issueComments.size()).isEqualTo(2);
        assertThat(issueComments.get(0).getIssueCommentContent()).isEqualTo("두번쨰 가는길 지금 이슈 댓글 테스트입니다");
    }
    @Test
    void 이슈_id를_통해_이슈에_달린_댓글을_조회할_수_있고_최신순으로_정렬된다() throws Exception{
        //when
        List<IssueComment> issueCommentByIssueId = issueCommentServiceImpl.getIssueCommentByIssueId(1L);
        //then
        assertThat(issueCommentByIssueId.size()).isEqualTo(2);
        assertThat(issueCommentByIssueId.get(0).getIssueCommentContent()).isEqualTo("두번쨰 가는길 지금 이슈 댓글 테스트입니다");
    }
    @Test
    void 댓글은_updateIssueComment_메서드를_통해_수정할_수_있다() throws Exception{
        //given
        IssueCommentUpdate issueCommentUpdate = IssueCommentUpdate.builder()
                .issueCommentId(1L)
                .issueCommentContent("수정된 댓글입니둥")
                .build();
        //when
        IssueComment issueComment = issueCommentServiceImpl.updateIssueComment(issueCommentUpdate);
        //then
        assertThat(issueComment.getIssueCommentContent()).isEqualTo("수정된 댓글입니둥");
    }
    @Test
    void 댓글은_deleteComment_메서드를_통해_삭제할_수_있다() throws Exception{
        //when
        issueCommentServiceImpl.deleteComment(1L);
        List<MyCommentSummary> issueComments = issueCommentServiceImpl.getIssueCommentsByMemberId();
        //then
        assertThat(issueComments.size()).isEqualTo(1);
    }

}