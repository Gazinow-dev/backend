package com.gazi.gazi_renew.issue.domain;

import com.gazi.gazi_renew.issue.domain.dto.CommentLikesCreate;
import com.gazi.gazi_renew.issue.domain.enums.IssueKeyword;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class CommentLikesTest {
    @Test
    void 댓글_좋아요는_CommentLikesCreate를_통해_생성할_수_있다() throws Exception{
        //given
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
        //when
        CommentLikes commentLikes = CommentLikes.from(issueComment, 1L);
        //then
        Assertions.assertThat(commentLikes.getIssueComment().getIssueCommentContent()).isEqualTo("가는길 지금 이슈 댓글 테스트입니다");


    }

}