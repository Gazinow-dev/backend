package com.gazi.gazi_renew.issue.controller.port;

import com.gazi.gazi_renew.issue.domain.IssueComment;
import com.gazi.gazi_renew.issue.domain.MyCommentSummary;
import com.gazi.gazi_renew.issue.domain.dto.IssueCommentCreate;
import com.gazi.gazi_renew.issue.domain.dto.IssueCommentUpdate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IssueCommentService {
    IssueComment saveComment(IssueCommentCreate issueCommentCreate) throws Exception;

    Page<MyCommentSummary> getIssueCommentsByMemberId(Pageable pageable);

    IssueComment updateIssueComment(IssueCommentUpdate issueCommentUpdate);
    void deleteComment(Long issueCommentId);

    Page<IssueComment> getIssueCommentByIssueId(Pageable pageable, Long issueId);

}
