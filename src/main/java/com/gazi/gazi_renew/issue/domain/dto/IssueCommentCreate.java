package com.gazi.gazi_renew.issue.domain.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
@Getter
public class IssueCommentCreate {
    private final Long issueId;
    @Size(max = 500, message = "댓글 내용은 500자를 넘을 수 없습니다.")
    private final String issueCommentContent;
    @Builder
    public IssueCommentCreate(Long issueId, String issueCommentContent) {
        this.issueId = issueId;
        this.issueCommentContent = issueCommentContent;
    }
}
