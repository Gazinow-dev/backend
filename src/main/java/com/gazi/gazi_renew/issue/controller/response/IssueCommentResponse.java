package com.gazi.gazi_renew.issue.controller.response;

import com.gazi.gazi_renew.issue.domain.IssueComment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class IssueCommentResponse {
    private final Long issueCommentId;
    private final String issueCommentContent;
    private final String createdBy;
    private final String agoTime;
    @Builder
    public IssueCommentResponse(Long issueCommentId, String issueCommentContent, String createdBy, String agoTime) {
        this.issueCommentId = issueCommentId;
        this.issueCommentContent = issueCommentContent;
        this.createdBy = createdBy;
        this.agoTime = agoTime;
    }

//    public static IssueCommentResponse from(IssueComment issueComment) {
//
//    }
}
