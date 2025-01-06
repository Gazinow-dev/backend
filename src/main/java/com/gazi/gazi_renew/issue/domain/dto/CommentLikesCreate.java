package com.gazi.gazi_renew.issue.domain.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentLikesCreate {
    private final Long issueCommentId;
    @Builder
    @JsonCreator
    public CommentLikesCreate(Long issueCommentId) {
        this.issueCommentId = issueCommentId;
    }
}
