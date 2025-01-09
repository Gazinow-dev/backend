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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id")
    private IssueEntity issueEntity;
    private String issueCommentContent;
    private String createdBy;
    private LocalDateTime createdAt;
    private int reportedCount;

    public static IssueCommentEntity from(IssueComment issueComment) {
        IssueCommentEntity issueCommentEntity = new IssueCommentEntity();
        issueCommentEntity.id = issueComment.getIssueCommentId();
        issueCommentEntity.memberId = issueComment.getMemberId();
        issueCommentEntity.issueEntity = IssueEntity.from(issueComment.getIssue());
        issueCommentEntity.issueCommentContent = issueComment.getIssueCommentContent();
        issueCommentEntity.createdBy = issueComment.getCreatedBy();
        issueCommentEntity.createdAt = issueComment.getCreatedAt();
        issueCommentEntity.reportedCount = issueComment.getReportedCount();

        return issueCommentEntity;
    }
    public IssueComment toModel() {
        return IssueComment.builder()
                .issueCommentId(id)
                .memberId(memberId)
                .issue(issueEntity.toModel())
                .issueCommentContent(issueCommentContent)
                .createdBy(createdBy)
                .createdAt(createdAt)
                .reportedCount(reportedCount)
                .build();
    }
}
