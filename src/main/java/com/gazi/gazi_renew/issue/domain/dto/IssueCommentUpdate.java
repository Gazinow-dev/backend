package com.gazi.gazi_renew.issue.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class IssueCommentUpdate {
    private final Long issueCommentId;
    private final String issueCommentContent;
    @Builder
    public IssueCommentUpdate(Long issueCommentId, String issueCommentContent) {
        this.issueCommentId = issueCommentId;
        this.issueCommentContent = issueCommentContent;
    }
}
