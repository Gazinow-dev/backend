package com.gazi.gazi_renew.issue.infrastructure;

import com.gazi.gazi_renew.issue.domain.IssueComment;
import com.gazi.gazi_renew.issue.infrastructure.entity.IssueCommentEntity;
import com.gazi.gazi_renew.issue.infrastructure.jpa.IssueCommentJpaRepository;
import com.gazi.gazi_renew.issue.service.port.IssueCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class IssueCommentRepositoryImpl implements IssueCommentRepository {
    private final IssueCommentJpaRepository issueCommentJpaRepository;
    @Override
    public IssueComment saveComment(IssueComment issueComment) {
        return issueCommentJpaRepository.save(IssueCommentEntity.from(issueComment)).toModel();
    }
    @Override
    public List<IssueComment> getIssueComments(Long memberId) {
        return issueCommentJpaRepository.findByMemberId(memberId).stream()
                .map(IssueCommentEntity::toModel).collect(Collectors.toList());
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
}
