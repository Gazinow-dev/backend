package com.gazi.gazi_renew.issue.service.port;

import com.gazi.gazi_renew.issue.domain.IssueComment;

import java.util.List;
import java.util.Optional;

public interface IssueCommentRepository {
    IssueComment saveComment(IssueComment issueComment);

    List<IssueComment> getIssueCommentsOrderByCreatedAt(Long memberId);

    void updateIssueComment(IssueComment issueComment);

    void deleteComment(Long issueCommentId);

    Optional<IssueComment> findByIssueCommentId(Long issueCommentId);

    int countByIssueId(Long issueId);

    List<IssueComment> getIssueCommentByIssueIdOrderByCreatedAt(Long issueId);
}
