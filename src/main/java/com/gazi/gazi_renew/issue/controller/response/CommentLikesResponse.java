package com.gazi.gazi_renew.issue.controller.response;

import com.gazi.gazi_renew.issue.domain.CommentLikes;
import com.gazi.gazi_renew.issue.domain.IssueComment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentLikesResponse {
    @Schema(description = "댓글 좋아요 ID", example = "1")
    private final Long commentLikesId;
    @Schema(description = "댓글 ID", example = "1")
    private final Long issueCommentId;
    @Builder
    public CommentLikesResponse(Long commentLikesId, Long issueCommentId) {
        this.commentLikesId = commentLikesId;
        this.issueCommentId = issueCommentId;
    }

    public static CommentLikesResponse from(CommentLikes commentLikes) {
        return CommentLikesResponse.builder()
                .commentLikesId(commentLikes.getCommentLikesId())
                .issueCommentId(commentLikes.getIssueComment().getIssueCommentId())
                .build();
    }
}
