package com.gazi.gazi_renew.issue.domain;

import com.gazi.gazi_renew.common.service.port.ClockHolder;
import com.gazi.gazi_renew.issue.domain.dto.IssueCommentCreate;
import com.gazi.gazi_renew.issue.domain.dto.IssueCommentUpdate;
import com.gazi.gazi_renew.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class IssueComment {
    private final Long issueCommentId;
    private final Long issueId;
    private final Long memberId;
    private final String issueCommentContent;
    private final String createdBy;
    private final LocalDateTime createdAt;

    @Builder
    public IssueComment(Long issueCommentId, Long issueId, Long memberId, String issueCommentContent, String createdBy, LocalDateTime createdAt) {
        this.issueCommentId = issueCommentId;
        this.issueId = issueId;
        this.memberId = memberId;
        this.issueCommentContent = issueCommentContent;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }
    public IssueComment update(IssueCommentUpdate issueCommentUpdate, ClockHolder clockHolder) {
        return IssueComment.builder()
                .issueCommentId(this.issueCommentId)
                .issueId(this.issueId)
                .memberId(this.memberId)
                .issueCommentContent(issueCommentUpdate.getIssueCommentContent())
                .createdBy(this.createdBy)
                .createdAt(clockHolder.now())
                .build();
    }

    public static IssueComment from(IssueCommentCreate issueCommentCreate, Member member, ClockHolder clockHolder) {
        return IssueComment.builder()
                .issueId(issueCommentCreate.getIssueId())
                .memberId(member.getId())
                .issueCommentContent(issueCommentCreate.getIssueCommentContent())
                .createdBy(member.getNickName())
                .createdAt(clockHolder.now())
                .build();
    }
}
