package com.gazi.gazi_renew.issue.domain;

import com.gazi.gazi_renew.issue.domain.dto.IssueCommentCreate;
import com.gazi.gazi_renew.issue.domain.dto.IssueCommentUpdate;
import com.gazi.gazi_renew.mock.TestClockHolder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class IssueCommentTest {
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
}