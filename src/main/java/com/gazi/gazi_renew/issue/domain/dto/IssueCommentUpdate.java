package com.gazi.gazi_renew.issue.domain.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
public class IssueCommentUpdate {
    private final Long issueCommentId;
    @Size(max = 500, message = "댓글 내용은 500자를 넘을 수 없습니다.")
    private final String issueCommentContent;
    @Builder
    public IssueCommentUpdate(Long issueCommentId, String issueCommentContent) {
        this.issueCommentId = issueCommentId;
        this.issueCommentContent = issueCommentContent;
    }
}
