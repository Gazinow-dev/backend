package com.gazi.gazi_renew.issue.domain;

import com.gazi.gazi_renew.issue.domain.dto.IssueCommentCreate;
import com.gazi.gazi_renew.issue.domain.dto.IssueCommentUpdate;
import com.gazi.gazi_renew.issue.domain.enums.IssueKeyword;
import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.domain.enums.Role;
import com.gazi.gazi_renew.mock.TestClockHolder;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class IssueCommentTest {
    private Validator validator;
    private Issue issue;
    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        issue = Issue.builder()
                .id(1L)
                .title("삼각지역 집회")
                .content("삼각지역 집회 가는길 지금 이슈 테스트")
                .startDate(LocalDateTime.parse("2024-11-15 08:29:00", formatter))
                .expireDate(LocalDateTime.parse("2024-11-15 10:29:00", formatter))
                .keyword(IssueKeyword.시위)
                .crawlingNo("1")
                .likeCount(10)
                .build();

    }

    @Test
    void IssueComment는_IssueCommentRequest를_통해_생성할_수_있다() throws Exception{
        //given
        IssueCommentCreate issueCommentCreate = IssueCommentCreate.builder()
                .issueId(1L)
                .issueCommentContent("이슈 댓글 테스트")
                .build();

        Member member = Member.builder()
                .id(1L)
                .email("mw310@naver.com")
                .password("temp")
                .nickName("minu")
                .role(Role.ROLE_USER)
                .pushNotificationEnabled(true)
                .mySavedRouteNotificationEnabled(true)
                .firebaseToken("temp")
                .build();
        LocalDateTime newTime = LocalDateTime.now();
        //when
        IssueComment issueComment = IssueComment.from(issueCommentCreate, issue, member, new TestClockHolder(newTime));
        //then
        assertThat(issueComment.getIssueCommentContent()).isEqualTo("이슈 댓글 테스트");
        assertThat(issueComment.getCreatedAt()).isEqualTo(newTime);
    }
    @Test
    void 댓글은_작성_후_수정할_수_있다() throws Exception{
        //given
        IssueCommentCreate issueCommentCreate = IssueCommentCreate.builder()
                .issueId(1L)
                .issueCommentContent("이슈 댓글 테스트")
                .build();
        Member member = Member.builder()
                .id(1L)
                .email("mw310@naver.com")
                .password("temp")
                .nickName("minu")
                .role(Role.ROLE_USER)
                .pushNotificationEnabled(true)
                .mySavedRouteNotificationEnabled(true)
                .firebaseToken("temp")
                .build();
        LocalDateTime newTime = LocalDateTime.now();
        IssueComment issueComment = IssueComment.from(issueCommentCreate, issue, member, new TestClockHolder(newTime));

        LocalDateTime updatedTime = LocalDateTime.now();
        IssueCommentUpdate issueCommentUpdate = IssueCommentUpdate.builder()
                .issueCommentId(1L)
                .issueCommentContent("이슈 댓글 수정 테스트")
                .build();
        //when
        IssueComment result = issueComment.update(issueCommentUpdate, new TestClockHolder(updatedTime));
        //then
        assertThat(result.getIssueCommentContent()).isEqualTo("이슈 댓글 수정 테스트");
        assertThat(result.getCreatedAt()).isEqualTo(updatedTime);
    }
    @Test
    void issueCommentContent_글자수_제약조건_초과_시_제약위반_테스트() {
        //given
        String tooLongContent = "A".repeat(501); // 501글자 생성
        IssueCommentCreate issueCommentCreate = IssueCommentCreate.builder()
                .issueId(1L)
                .issueCommentContent(tooLongContent) // 500자 초과
                .build();

        //when
        Set<ConstraintViolation<IssueCommentCreate>> violations = validator.validate(issueCommentCreate);

        //then
        assertThat(violations).hasSize(1); // 제약 조건 위반이 1개여야 한다.
        assertThat(violations.iterator().next().getMessage()).isEqualTo("댓글 내용은 500자를 넘을 수 없습니다.");
    }
    @Test
    void issueCommentContent_글자수_제약조건_통과_테스트() {
        //given
        String validContent = "A".repeat(500); // 500글자 생성
        IssueCommentCreate issueCommentCreate = IssueCommentCreate.builder()
                .issueId(1L)
                .issueCommentContent(validContent) // 500자 이내
                .build();

        //when
        Set<ConstraintViolation<IssueCommentCreate>> violations = validator.validate(issueCommentCreate);

        //then
        assertThat(violations).isEmpty(); // 제약 조건 위반이 없어야 한다.
    }
    @Test
    void getTime_테스트_getTime_테스트_댓글_단지_1분_미만은_지금으로_표현() {
        //given
        IssueCommentCreate issueCommentCreate = IssueCommentCreate.builder()
                .issueId(1L)
                .issueCommentContent("이슈 댓글 테스트")
                .build();
        Member member = Member.builder()
                .id(1L)
                .email("mw310@naver.com")
                .password("temp")
                .nickName("minu")
                .role(Role.ROLE_USER)
                .pushNotificationEnabled(true)
                .mySavedRouteNotificationEnabled(true)
                .firebaseToken("temp")
                .build();

        LocalDateTime newTime = LocalDateTime.now().minusSeconds(35); // 35초 전 생성
        IssueComment issueComment = IssueComment.from(issueCommentCreate, issue, member, new TestClockHolder(newTime));

        //when
        String result = issueComment.formatTime(new TestClockHolder(LocalDateTime.now()));
        //then
        assertThat(result).isEqualTo("지금");
    }
    @Test
    void getTime_테스트_댓글_단지_1분_이상에서_1시간_미만은_몇분전으로() {
        //given
        IssueCommentCreate issueCommentCreate = IssueCommentCreate.builder()
                .issueId(1L)
                .issueCommentContent("이슈 댓글 테스트")
                .build();
        Member member = Member.builder()
                .id(1L)
                .email("mw310@naver.com")
                .password("temp")
                .nickName("minu")
                .role(Role.ROLE_USER)
                .pushNotificationEnabled(true)
                .mySavedRouteNotificationEnabled(true)
                .firebaseToken("temp")
                .build();

        LocalDateTime newTime = LocalDateTime.now().minusMinutes(5); // 5분 전 생성
        IssueComment issueComment = IssueComment.from(issueCommentCreate, issue, member, new TestClockHolder(newTime));
        //when
        String result = issueComment.formatTime(new TestClockHolder(LocalDateTime.now()));
        //then
        assertThat(result).isEqualTo("5분 전");
    }
    @Test
    void getTime_테스트_댓글_단지_1시간_이상에서_1일_미만은_작성된_시간() {
        //given
        IssueCommentCreate issueCommentCreate = IssueCommentCreate.builder()
                .issueId(1L)
                .issueCommentContent("이슈 댓글 테스트")
                .build();
        Member member = Member.builder()
                .id(1L)
                .email("mw310@naver.com")
                .password("temp")
                .nickName("minu")
                .role(Role.ROLE_USER)
                .pushNotificationEnabled(true)
                .mySavedRouteNotificationEnabled(true)
                .firebaseToken("temp")
                .build();

        LocalDateTime fixedNow = LocalDateTime.of(2025, 1, 3, 10, 30);
        LocalDateTime newTime = fixedNow.minusHours(2);

        IssueComment issueComment = IssueComment.from(issueCommentCreate, issue, member, new TestClockHolder(newTime));
        //when
        String result = issueComment.formatTime(new TestClockHolder(fixedNow));
        //then
        String expectedTime = "오전 08:30"; // 예상 결과값
        assertThat(result).isEqualTo(expectedTime);
    }
    @Test
    void getTime_테스트_댓글_단지_1일_이상은_몇일_전으로() {
        //given
        IssueCommentCreate issueCommentCreate = IssueCommentCreate.builder()
                .issueId(1L)
                .issueCommentContent("이슈 댓글 테스트")
                .build();
        Member member = Member.builder()
                .id(1L)
                .email("mw310@naver.com")
                .password("temp")
                .nickName("minu")
                .role(Role.ROLE_USER)
                .pushNotificationEnabled(true)
                .mySavedRouteNotificationEnabled(true)
                .firebaseToken("temp")
                .build();

        LocalDateTime newTime = LocalDateTime.now().minusDays(2); // 2일 전 생성
        IssueComment issueComment = IssueComment.from(issueCommentCreate, issue, member, new TestClockHolder(newTime));
        //when
        String result = issueComment.formatTime(new TestClockHolder(LocalDateTime.now()));
        //then
        assertThat(result).isEqualTo("2일 전");
    }
}

