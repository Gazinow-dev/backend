package com.gazi.gazi_renew.issue.service.port;

import com.gazi.gazi_renew.issue.domain.IssueComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface IssueCommentRepository {
    IssueComment saveComment(IssueComment issueComment);

    Page<IssueComment> getIssueComments(Pageable pageable, Long memberId);

    void updateIssueComment(IssueComment issueComment);

    void deleteComment(Long issueCommentId);

    Optional<IssueComment> findByIssueCommentId(Long issueCommentId);

    int countByIssueId(Long issueId);

    Page<IssueComment> getIssueCommentByIssueId(Pageable pageable, Long issueId);

    void addReportedCount(IssueComment issueComment);
}
