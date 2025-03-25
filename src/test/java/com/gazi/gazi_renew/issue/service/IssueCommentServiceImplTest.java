package com.gazi.gazi_renew.issue.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gazi.gazi_renew.issue.domain.CommentLikes;
import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.domain.IssueComment;
import com.gazi.gazi_renew.issue.domain.MyCommentSummary;
import com.gazi.gazi_renew.issue.domain.dto.IssueCommentCreate;
import com.gazi.gazi_renew.issue.domain.dto.IssueCommentUpdate;
import com.gazi.gazi_renew.issue.domain.enums.IssueKeyword;
import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.domain.enums.Role;
import com.gazi.gazi_renew.mock.common.FakeRedisUtilServiceImpl;
import com.gazi.gazi_renew.mock.common.FakeSecurityUtil;
import com.gazi.gazi_renew.mock.common.TestClockHolder;
import com.gazi.gazi_renew.mock.issue.FakeCommentLikesRepository;
import com.gazi.gazi_renew.mock.issue.FakeIssueCommentRepository;
import com.gazi.gazi_renew.mock.issue.FakeIssueRepository;
import com.gazi.gazi_renew.mock.member.FakeMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

class IssueCommentServiceImplTest {
    private IssueCommentServiceImpl issueCommentServiceImpl;
    private FakeIssueCommentRepository fakeIssueCommentRepository;
    @BeforeEach
    void init() {
        ObjectMapper mapper = new ObjectMapper();
        fakeIssueCommentRepository = new FakeIssueCommentRepository();
        FakeMemberRepository fakeMemberRepository = new FakeMemberRepository();
        FakeIssueRepository fakeIssueRepository = new FakeIssueRepository();
        LocalDateTime newTime = LocalDateTime.now();
        TestClockHolder testClockHolder = new TestClockHolder(newTime);
        FakeSecurityUtil fakeSecurityUtil = new FakeSecurityUtil();
        FakeCommentLikesRepository fakeCommentLikesRepository = new FakeCommentLikesRepository();
        FakeRedisUtilServiceImpl fakeRedisUtilService = new FakeRedisUtilServiceImpl(mapper);

        this.issueCommentServiceImpl = new IssueCommentServiceImpl(fakeIssueCommentRepository, fakeMemberRepository
                , testClockHolder, fakeSecurityUtil, fakeIssueRepository, fakeRedisUtilService, fakeCommentLikesRepository);
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

        CommentLikes commentLikes = CommentLikes.builder()
                .commentLikesId(1L)
                .issueComment(issueComment)
                .memberId(1L)
                .build();
        fakeCommentLikesRepository.save(commentLikes);

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
        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "createdAt"));
        //when
        Page<MyCommentSummary> issueComments = issueCommentServiceImpl.getIssueCommentsByMemberId(pageable);
        //then
        assertThat(issueComments.getTotalElements()).isEqualTo(2); // 전체 데이터 수 검증
        assertThat(issueComments.getTotalPages()).isEqualTo(1);    // 전체 페이지 수 검증
        assertThat(issueComments.getContent().size()).isEqualTo(2); // 현재 페이지 데이터 크기 검증
        assertThat(issueComments.getContent().get(0).getIssueCommentContent())
                .isEqualTo("두번쨰 가는길 지금 이슈 댓글 테스트입니다"); // 첫 번째 댓글 내용 검증
    }
    @Test
    void 이슈_id를_통해_이슈에_달린_댓글을_조회할_수_있고_최신순으로_정렬된다() throws Exception{
        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "createdAt"));
        //when
        Page<IssueComment> issueCommentByIssueId = issueCommentServiceImpl.getIssueCommentByIssueId(pageable, 1L);
        //then
        assertThat(issueCommentByIssueId.getTotalElements()).isEqualTo(2);
        assertThat(issueCommentByIssueId.getContent().get(0).getIssueCommentContent()).isEqualTo("두번쨰 가는길 지금 이슈 댓글 테스트입니다");
    }
    @Test
    void 이슈_id를_통해_이슈에_달린_댓글을_조회시_댓글_신고_횟수가_3회_이상인_댓글은_즉시_숨김_처리가_된다() throws Exception{
        IssueCommentCreate issueComment = IssueCommentCreate.builder()
                .issueId(1L)
                .issueCommentContent("새로운 가는길 지금 이슈 댓글 테스트입니다")
                .build();
        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "createdAt"));
        //when
        IssueComment issueComment1 = issueCommentServiceImpl.saveComment(issueComment);

        Page<IssueComment> issueCommentByIssueId = issueCommentServiceImpl.getIssueCommentByIssueId(pageable, 1L);
        assertThat(issueCommentByIssueId.getTotalElements()).isEqualTo(3);
        for (int i = 0; i < 3; i++) {
            issueComment1 = issueComment1.addReportedCount();
            fakeIssueCommentRepository.updateReportedCount(issueComment1);
        }
        Page<IssueComment> result = issueCommentServiceImpl.getIssueCommentByIssueId(pageable, 1L);
        //then
        assertThat(result.getTotalElements()).isEqualTo(2);
    }
    @Test
    void getIssueCommentByIssueId_메서드는_좋아요_갯수_및_상태까지_가져온다() throws Exception{
        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "createdAt"));
        //when
        Page<IssueComment> issueCommentByIssueId = issueCommentServiceImpl.getIssueCommentByIssueId(pageable, 1L);
        //then
        assertThat(issueCommentByIssueId.getContent().get(1).getLikesCount()).isEqualTo(1);
        assertThat(issueCommentByIssueId.getContent().get(1).isMine()).isEqualTo(true);
        assertThat(issueCommentByIssueId.getContent().get(1).isLiked()).isEqualTo(true);

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
        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "createdAt"));
        //when
        issueCommentServiceImpl.deleteComment(1L);
        Page<MyCommentSummary> issueComments = issueCommentServiceImpl.getIssueCommentsByMemberId(pageable);
        //then
        assertThat(issueComments.getTotalElements()).isEqualTo(1);
    }

}