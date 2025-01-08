package com.gazi.gazi_renew.issue.infrastructure.jpa;

import com.gazi.gazi_renew.issue.infrastructure.entity.CommentLikesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikesJpaRepository extends JpaRepository<CommentLikesEntity, Long> {
    boolean existsByIssueCommentEntityIdAndMemberId(Long issueCommentId, Long memberId);

    Long countByIssueCommentEntityId(Long issueCommentId);
}
