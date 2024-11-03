package com.gazi.gazi_renew.issue.infrastructure;

import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.infrastructure.jpa.IssueJpaRepository;
import com.gazi.gazi_renew.issue.service.port.IssueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class IssueRepositoryImpl implements IssueRepository {
    private final IssueJpaRepository issueJpaRepository;

    @Override
    public boolean existsByCrawlingNo(String crawlingNo) {
        return issueJpaRepository.existsByCrawlingNo(crawlingNo);
    }

    @Override
    public List<Issue> findTopIssuesByLikesCount(int likesCount, Pageable pageable) {
        return issueJpaRepository.findTopIssuesByLikesCount(likesCount, pageable).stream()
                .map(IssueEntity::toModel).collect(Collectors.toList());
    }

    @Override
    public Issue save(Issue issue) {
        return issueJpaRepository.save(IssueEntity.from(issue)).toModel();
    }

    @Override
    public Page<Issue> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<Issue> findById(Long id) {
        return Optional.empty();
    }
}
