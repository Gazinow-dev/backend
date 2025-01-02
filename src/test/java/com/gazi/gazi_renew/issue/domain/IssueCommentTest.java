package com.gazi.gazi_renew.issue.domain;

import com.gazi.gazi_renew.issue.domain.dto.IssueCommentCreate;
import com.gazi.gazi_renew.issue.domain.dto.IssueCommentUpdate;
import com.gazi.gazi_renew.mock.TestClockHolder;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class IssueCommentTest {
    private Validator validator;
    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void IssueComment는_IssueCommentRequest를_통해_생성할_수_있다() throws Exception{
        //given
        IssueCommentCreate issueCommentCreate = IssueCommentCreate.builder()
                .issueId(1L)
                .issueCommentContent("이슈 댓글 테스트")
                .build();
        LocalDateTime newTime = LocalDateTime.now();
        //when
        IssueComment issueComment = IssueComment.from(issueCommentCreate, 1L, "민우", new TestClockHolder(newTime));
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
        LocalDateTime newTime = LocalDateTime.now();
        IssueComment issueComment = IssueComment.from(issueCommentCreate, 1L, "민우", new TestClockHolder(newTime));

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
}