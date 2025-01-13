package com.gazi.gazi_renew.issue.service.port;

import com.gazi.gazi_renew.issue.domain.CommentLikes;
import com.gazi.gazi_renew.issue.domain.IssueComment;

public interface CommentLikesRepository {
    boolean existByIssueCommentAndMemberId(IssueComment issueComment, Long memberId);

    CommentLikes save(CommentLikes from);

    int countByIssueCommentId(Long issueCommentId);
    void deleteByIssueCommentIdAndMemberId(Long issueCommentId, Long memberId);
}
