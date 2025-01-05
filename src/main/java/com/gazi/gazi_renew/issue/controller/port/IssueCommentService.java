package com.gazi.gazi_renew.issue.controller.port;

import com.gazi.gazi_renew.issue.domain.IssueComment;
import com.gazi.gazi_renew.issue.domain.MyCommentSummary;
import com.gazi.gazi_renew.issue.domain.dto.IssueCommentCreate;
import com.gazi.gazi_renew.issue.domain.dto.IssueCommentUpdate;

import java.util.List;

public interface IssueCommentService {
    IssueComment saveComment(IssueCommentCreate issueCommentCreate);

    List<MyCommentSummary> getIssueCommentsByMemberId();

    IssueComment updateIssueComment(IssueCommentUpdate issueCommentUpdate);
    void deleteComment(Long issueCommentId);

    List<IssueComment> getIssueCommentByIssueId(Long issueId);

}
