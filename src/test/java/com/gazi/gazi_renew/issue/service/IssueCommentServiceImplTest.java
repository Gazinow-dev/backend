package com.gazi.gazi_renew.issue.service;

import com.gazi.gazi_renew.issue.domain.IssueComment;
import com.gazi.gazi_renew.issue.domain.dto.IssueCommentCreate;
import com.gazi.gazi_renew.issue.domain.dto.IssueCommentUpdate;
import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.domain.enums.Role;
import com.gazi.gazi_renew.mock.FakeIssueCommentRepository;
import com.gazi.gazi_renew.mock.FakeMemberRepository;
import com.gazi.gazi_renew.mock.FakeSecurityUtil;
import com.gazi.gazi_renew.mock.TestClockHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class IssueCommentServiceImplTest {
    private IssueCommentServiceImpl issueCommentServiceImpl;
    @BeforeEach
    void init() {
        FakeIssueCommentRepository fakeIssueCommentRepository = new FakeIssueCommentRepository();
        FakeMemberRepository fakeMemberRepository = new FakeMemberRepository();
        LocalDateTime newTime = LocalDateTime.now();
        TestClockHolder testClockHolder = new TestClockHolder(newTime);
        FakeSecurityUtil fakeSecurityUtil = new FakeSecurityUtil();

        this.issueCommentServiceImpl = new IssueCommentServiceImpl(fakeIssueCommentRepository, fakeMemberRepository
                , testClockHolder, fakeSecurityUtil);
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

        IssueComment issueComment = IssueComment.builder()
                .issueCommentId(1L)
                .issueId(1L)
                .memberId(1L)
                .issueCommentContent("가는길 지금 이슈 댓글 테스트입니다")
                .createdBy("이민우")
                .createdAt(newTime)
                .build();

        fakeSecurityUtil.addEmail("mw310@naver.com");
        fakeMemberRepository.save(member1);
        fakeIssueCommentRepository.saveComment(issueComment);
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
    void 나의_정보에서_내가_쓴_댓글을_확인할_수_있다() throws Exception{
        //when
        List<IssueComment> issueComments = issueCommentServiceImpl.getIssueComments();
        //then
        assertThat(issueComments.size()).isEqualTo(1);
        assertThat(issueComments.get(0).getIssueCommentContent()).isEqualTo("가는길 지금 이슈 댓글 테스트입니다");
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
        List<IssueComment> issueComments = issueCommentServiceImpl.getIssueComments();
        //then
        assertThat(issueComments.size()).isEqualTo(0);
    }
}