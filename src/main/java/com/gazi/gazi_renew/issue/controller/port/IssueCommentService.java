package com.gazi.gazi_renew.issue.controller.port;

import com.gazi.gazi_renew.issue.domain.IssueComment;
import com.gazi.gazi_renew.issue.domain.dto.IssueCommentCreate;
import com.gazi.gazi_renew.issue.domain.dto.IssueCommentDelete;
import com.gazi.gazi_renew.issue.domain.dto.IssueCommentUpdate;

import java.util.List;

public interface IssueCommentService {
    IssueComment saveComment(IssueCommentCreate issueCommentCreate);

    List<IssueComment> getIssueComments();

    IssueComment updateIssueComment(IssueCommentUpdate issueCommentUpdate);
    void deleteComment(IssueCommentDelete issueCommentDelete);
}
