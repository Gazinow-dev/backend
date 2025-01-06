package com.gazi.gazi_renew.issue.domain;

import com.gazi.gazi_renew.issue.domain.dto.CommentLikesCreate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentLikes {
    private final Long commentLikesId;
    private final IssueComment issueComment;
    private final Long memberId;
    @Builder
    public CommentLikes(Long commentLikesId, IssueComment issueComment, Long memberId) {
        this.commentLikesId = commentLikesId;
        this.issueComment = issueComment;
        this.memberId = memberId;
    }
    public static CommentLikes from(IssueComment issueComment, Long memberId) {
        return CommentLikes.builder()
                .issueComment(issueComment)
                .memberId(memberId)
                .build();
    }
}
