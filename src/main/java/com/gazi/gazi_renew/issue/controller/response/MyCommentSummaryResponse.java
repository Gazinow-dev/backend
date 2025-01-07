package com.gazi.gazi_renew.issue.controller.response;

import com.gazi.gazi_renew.issue.domain.MyCommentSummary;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class MyCommentSummaryResponse {
    @Schema(description = "댓글 ID", example = "댓글 ID: 1")
    private final Long issueCommentId;
    @Schema(description = "이슈 ID", example = "이슈 ID: 1")
    private final Long issueId;
    @Schema(description = "댓글 내용", example = "댓글 내용입니다...")
    private final String issueCommentContent;
    @Schema(description = "작성자", example = "가는길 지금 관리자")
    private final String createdBy;
    @Schema(description = "등록시간", example = "2분 전, 지금, 1일 전")
    private final String agoTime;
    @Schema(description = "이슈 제목", example = "[9호선 시위] ~~")
    private final String issueTitle;
    @Schema(description = "이슈 종류", example = "시위, 자연재해 , ...")
    private final String issueKeyword;
    @Schema(description = "댓글 갯수", example = "5")
    private final int commentsCount;
    @Schema(description = "이슈 도움돼요 갯수", example = "2")
    private final int issueLikesCount;
    @Schema(description = "댓글 좋아요 갯수", example = "2")
    private final int commentLikesCount;
    @Builder
    public MyCommentSummaryResponse(Long issueCommentId, Long issueId, String issueCommentContent, String createdBy, String agoTime, String issueTitle, String issueKeyword, int commentsCount, int issueLikesCount, int commentLikesCount) {
        this.issueCommentId = issueCommentId;
        this.issueId = issueId;
        this.issueCommentContent = issueCommentContent;
        this.createdBy = createdBy;
        this.agoTime = agoTime;
        this.issueTitle = issueTitle;
        this.issueKeyword = issueKeyword;
        this.commentsCount = commentsCount;
        this.issueLikesCount = issueLikesCount;
        this.commentLikesCount = commentLikesCount;
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
                .commentLikesCount(myCommentSummary.getCommentLikesCount())
                .build()
        );
    }
}
