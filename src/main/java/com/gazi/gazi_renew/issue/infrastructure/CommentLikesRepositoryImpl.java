package com.gazi.gazi_renew.issue.infrastructure;

import com.gazi.gazi_renew.issue.domain.CommentLikes;
import com.gazi.gazi_renew.issue.domain.IssueComment;
import com.gazi.gazi_renew.issue.infrastructure.entity.CommentLikesEntity;
import com.gazi.gazi_renew.issue.infrastructure.jpa.CommentLikesJpaRepository;
import com.gazi.gazi_renew.issue.service.port.CommentLikesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentLikesRepositoryImpl implements CommentLikesRepository {
    private final CommentLikesJpaRepository commentLikesJpaRepository;

    @Override
    public boolean existByIssueCommentIdAndMemberId(Long issueCommentId, Long memberId) {
        return commentLikesJpaRepository.existsByIssueCommentEntityIdAndMemberId(issueCommentId, memberId);
    }
    @Override
    public CommentLikes save(CommentLikes commentLikes) {
        return commentLikesJpaRepository.save(CommentLikesEntity.from(commentLikes)).toModel();
    }
    @Override
    public void deleteByIssueCommentIdAndMemberId(Long issueCommentId, Long memberId) {
        commentLikesJpaRepository.deleteByIssueCommentEntityIdAndMemberId(issueCommentId, memberId);
    }
    @Override
    public int countByIssueCommentId(Long issueCommentId) {
        return commentLikesJpaRepository.countByIssueCommentEntityId(issueCommentId).intValue();
    }
}
