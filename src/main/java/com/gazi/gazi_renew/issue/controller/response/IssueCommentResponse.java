package com.gazi.gazi_renew.issue.controller.response;

import com.gazi.gazi_renew.issue.domain.IssueComment;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

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

    public static IssueCommentResponse from(IssueComment issueComment) {
        return IssueCommentResponse.builder()
                .issueCommentId(issueComment.getIssueCommentId())
                .issueCommentContent(issueComment.getIssueCommentContent())
                .createdBy(issueComment.getCreatedBy())
                .agoTime(issueComment.formatTime())
                .build();
    }
    public static List<IssueCommentResponse> fromList(List<IssueComment> issueCommentList) {
        return issueCommentList.stream()
                .map(issueComment -> IssueCommentResponse.builder()
                        .issueCommentId(issueComment.getIssueCommentId())
                        .issueCommentContent(issueComment.getIssueCommentContent())
                        .createdBy(issueComment.getCreatedBy())
                        .agoTime(issueComment.formatTime())
                        .build()
                )
                .collect(Collectors.toList());
    }
}
