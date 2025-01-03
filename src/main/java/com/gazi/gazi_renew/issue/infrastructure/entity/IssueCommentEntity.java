package com.gazi.gazi_renew.issue.infrastructure.entity;

import com.gazi.gazi_renew.issue.domain.IssueComment;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name="issue_comment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IssueCommentEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long memberId;
    private Long issueId;
    private String issueCommentContent;
    private String createdBy;
    private LocalDateTime createdAt;

    public static IssueCommentEntity from(IssueComment issueComment) {
        IssueCommentEntity issueCommentEntity = new IssueCommentEntity();
        issueCommentEntity.id = issueComment.getIssueCommentId();
        issueCommentEntity.memberId = issueComment.getMemberId();
        issueCommentEntity.issueId = issueComment.getIssueId();
        issueCommentEntity.issueCommentContent = issueComment.getIssueCommentContent();
        issueCommentEntity.createdBy = issueComment.getCreatedBy();
        issueCommentEntity.createdAt = issueComment.getCreatedAt();

        return issueCommentEntity;
    }
    public IssueComment toModel() {
        return IssueComment.builder()
                .issueCommentId(id)
                .memberId(memberId)
                .issueId(issueId)
                .issueCommentContent(issueCommentContent)
                .createdBy(createdBy)
                .createdAt(createdAt)
                .build();
    }
}
