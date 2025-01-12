package com.gazi.gazi_renew.issue.service;

import com.gazi.gazi_renew.admin.service.DiscordNotifier;
import com.gazi.gazi_renew.common.exception.CustomException;
import com.gazi.gazi_renew.issue.domain.CommentLikes;
import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.domain.IssueComment;
import com.gazi.gazi_renew.issue.domain.dto.CommentLikesCreate;
import com.gazi.gazi_renew.issue.domain.enums.IssueKeyword;
import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.domain.enums.Role;
import com.gazi.gazi_renew.mock.issue.FakeCommentLikesRepository;
import com.gazi.gazi_renew.mock.issue.FakeIssueCommentRepository;
import com.gazi.gazi_renew.mock.member.FakeMemberRepository;
import com.gazi.gazi_renew.mock.common.FakeSecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CommentLikesServiceImplTest {
    CommentLikesServiceImpl commentLikesServiceImpl;

    @BeforeEach
    void init() {
        FakeCommentLikesRepository fakeCommentLikesRepository = new FakeCommentLikesRepository();
        FakeMemberRepository fakeMemberRepository = new FakeMemberRepository();
        FakeIssueCommentRepository fakeIssueCommentRepository = new FakeIssueCommentRepository();
        FakeSecurityUtil fakeSecurityUtil = new FakeSecurityUtil();

        this.commentLikesServiceImpl = new CommentLikesServiceImpl(fakeCommentLikesRepository, fakeMemberRepository
                , fakeIssueCommentRepository, fakeSecurityUtil);

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

        IssueComment issueComment = IssueComment.builder()
                .issueCommentId(1L)
                .issue(issue)
                .memberId(1L)
                .issueCommentContent("가는길 지금 이슈 댓글 테스트입니다")
                .createdBy("이민우")
                .createdAt(LocalDateTime.now())
                .build();
        CommentLikes commentLikes = CommentLikes.builder()
                .commentLikesId(1L)
                .issueComment(issueComment)
                .memberId(1L)
                .build();

        fakeSecurityUtil.addEmail("mw310@naver.com");
        fakeMemberRepository.save(member1);
        fakeIssueCommentRepository.saveComment(issueComment);

        fakeCommentLikesRepository.save(commentLikes);
    }
    @Test
    void 하나의_댓글에는_사용자_한_명이_한_번만_좋아요를_누를_수_있다() throws Exception{
        //given
        //이미 셋업데이터 1개가 추가 돼있음
        CommentLikesCreate commentLikesCreate = CommentLikesCreate.builder()
                .issueCommentId(1L)
                .build();
        //when
        //then
        assertThatThrownBy(() -> commentLikesServiceImpl.addLike(commentLikesCreate)).isInstanceOf(CustomException.class);
    }
}