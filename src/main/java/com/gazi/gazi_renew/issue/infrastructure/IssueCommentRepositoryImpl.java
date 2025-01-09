package com.gazi.gazi_renew.issue.infrastructure;

import com.gazi.gazi_renew.issue.domain.IssueComment;
import com.gazi.gazi_renew.issue.infrastructure.entity.IssueCommentEntity;
import com.gazi.gazi_renew.issue.infrastructure.jpa.IssueCommentJpaRepository;
import com.gazi.gazi_renew.issue.service.port.IssueCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class IssueCommentRepositoryImpl implements IssueCommentRepository {
    private final IssueCommentJpaRepository issueCommentJpaRepository;
    @Override
    public IssueComment saveComment(IssueComment issueComment) {
        return issueCommentJpaRepository.save(IssueCommentEntity.from(issueComment)).toModel();
    }
    @Override
    public Page<IssueComment> getIssueComments(Pageable pageable, Long memberId) {
        return issueCommentJpaRepository.findByMemberId(memberId, pageable)
                .map(IssueCommentEntity::toModel);
    }
    @Override
    public void updateIssueComment(IssueComment issueComment) {
        issueCommentJpaRepository.updateIssueCommentEntityContent(issueComment.getIssueCommentId(), issueComment.getIssueCommentContent());
    }
    @Override
    public void deleteComment(Long issueCommentId) {
        issueCommentJpaRepository.deleteById(issueCommentId);
    }
    @Override
    public Optional<IssueComment> findByIssueCommentId(Long issueCommentId) {
        return issueCommentJpaRepository.findById(issueCommentId).map(IssueCommentEntity::toModel);
    }

    @Override
    public int countByIssueId(Long issueId) {
        return issueCommentJpaRepository.countByIssueEntityId(issueId);
    }

    @Override
    public Page<IssueComment> getIssueCommentByIssueId(Pageable pageable, Long issueId) {
        return issueCommentJpaRepository.findByIssueEntityId(issueId, pageable)
                .map(IssueCommentEntity::toModel);
    }

    @Override
    public void addReportedCount(IssueComment issueComment) {
        issueCommentJpaRepository.updateReportedCount(issueComment.getIssueCommentId(), issueComment.getReportedCount());
    }
}
