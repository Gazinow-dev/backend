package com.gazi.gazi_renew.issue.service.port;

import com.gazi.gazi_renew.issue.domain.CommentLikes;
import com.gazi.gazi_renew.issue.domain.IssueComment;

public interface CommentLikesRepository {
    boolean existByIssueCommentAndMemberId(IssueComment issueComment, Long memberId);

    CommentLikes save(CommentLikes from);

    void deleteByCommentLikesId(Long commentLikesId);
    void deleteByIssueCommentId(Long issueCommentId);

    int countByIssueCommentId(Long issueCommentId);
}
