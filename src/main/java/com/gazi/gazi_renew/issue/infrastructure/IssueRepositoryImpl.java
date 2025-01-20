package com.gazi.gazi_renew.issue.infrastructure;

import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.infrastructure.entity.IssueEntity;
import com.gazi.gazi_renew.issue.infrastructure.jpa.IssueJpaRepository;
import com.gazi.gazi_renew.issue.service.port.IssueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Comparator;
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
        List<Issue> collect = issueJpaRepository.findAll()
                .stream()
                .map(IssueEntity::toModel)
                .sorted(Comparator.comparing(Issue::getStartDate).reversed())
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), collect.size());
        List<Issue> pagedList = collect.subList(start, end);

        return new PageImpl<>(pagedList, pageable, collect.size());
    }

    @Override
    public Optional<Issue> findById(Long id) {
        return issueJpaRepository.findById(id).map(IssueEntity::toModel);
    }

    @Override
    public void updateIssue(Issue issue) {
        issueJpaRepository.updateContentAndTitle(issue.getId(), issue.getTitle(), issue.getContent());
    }

    @Override
    public void updateLikeCount(Issue issue) {
        issueJpaRepository.updateLikeCount(issue.getId(), issue.getLikeCount());
    }

    @Override
    public void flush() {
        issueJpaRepository.flush();
    }

    @Override
    public void deleteIssue(Long id) {
        issueJpaRepository.deleteById(id);
    }

    @Override
    public void updateStartDateAndExpireDate(Long id, LocalDateTime startDate, LocalDateTime expireDate) {
        issueJpaRepository.updateStartDateAndExpireDate(id, startDate, expireDate);
    }
}