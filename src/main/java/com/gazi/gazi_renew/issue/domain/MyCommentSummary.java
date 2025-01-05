package com.gazi.gazi_renew.issue.domain;

import com.gazi.gazi_renew.common.service.port.ClockHolder;
import lombok.Builder;
import lombok.Getter;

/**
 * 내가 작성한 댓글 요약 도메인
 */
@Getter
public class MyCommentSummary {
    private final Long issueCommentId;
    private final Long issueId;
    private final String issueCommentContent;
    private final String createdBy;
    private final String agoTime;
    private final String issueTitle;
    private final String issueKeyword;
    private final int commentsCount;
    private final int issueLikeCount;
    @Builder
    public MyCommentSummary(Long issueCommentId, Long issueId, String issueCommentContent, String createdBy, String agoTime, String issueTitle, String issueKeyword, int commentsCount, int issueLikeCount) {
        this.issueCommentId = issueCommentId;
        this.issueId = issueId;
        this.issueCommentContent = issueCommentContent;
        this.createdBy = createdBy;
        this.agoTime = agoTime;
        this.issueTitle = issueTitle;
        this.issueKeyword = issueKeyword;
        this.commentsCount = commentsCount;
        this.issueLikeCount = issueLikeCount;
    }

    public static MyCommentSummary from(IssueComment issueComment, int commentCount, ClockHolder clockHolder) {
        return MyCommentSummary.builder()
                .issueCommentId(issueComment.getIssueCommentId())
                .issueId(issueComment.getIssue().getId())
                .issueCommentContent(issueComment.getIssueCommentContent())
                .createdBy(issueComment.getCreatedBy())
                .agoTime(issueComment.formatTime(clockHolder))
                .commentsCount(commentCount)
                .issueTitle(issueComment.getIssue().getTitle())
                .issueKeyword(issueComment.getIssue().getKeyword().toString())
                .issueLikeCount(issueComment.getIssue().getLikeCount())
                .build();
    }
}
