package com.gazi.gazi_renew.issue.controller.response;

import com.gazi.gazi_renew.issue.domain.MyCommentSummary;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class MyCommentSummaryResponse {
    private final Long issueCommentId;
    private final Long issueId;
    private final String issueCommentContent;
    private final String createdBy;
    private final String agoTime;
    private final String issueTitle;
    private final String issueKeyword;
    private final int commentsCount;
    private final int issueLikesCount;
    @Builder
    public MyCommentSummaryResponse(Long issueCommentId, Long issueId, String issueCommentContent, String createdBy, String agoTime, String issueTitle, String issueKeyword, int commentsCount, int issueLikesCount) {
        this.issueCommentId = issueCommentId;
        this.issueId = issueId;
        this.issueCommentContent = issueCommentContent;
        this.createdBy = createdBy;
        this.agoTime = agoTime;
        this.issueTitle = issueTitle;
        this.issueKeyword = issueKeyword;
        this.commentsCount = commentsCount;
        this.issueLikesCount = issueLikesCount;
    }
    public static Page<MyCommentSummaryResponse> fromPage(Page<MyCommentSummary> myCommentSummaryList) {
        return myCommentSummaryList.map(myCommentSummary -> MyCommentSummaryResponse.builder()
                .issueCommentId(myCommentSummary.getIssueCommentId())
                .issueId(myCommentSummary.getIssueId())
                .issueCommentContent(myCommentSummary.getIssueCommentContent())
                .createdBy(myCommentSummary.getCreatedBy())
                .agoTime(myCommentSummary.getAgoTime())
                .issueTitle(myCommentSummary.getIssueTitle())
                .issueKeyword(myCommentSummary.getIssueKeyword())
                .commentsCount(myCommentSummary.getCommentsCount())
                .issueLikesCount(myCommentSummary.getIssueLikeCount())
                .build()
        );
    }
}
