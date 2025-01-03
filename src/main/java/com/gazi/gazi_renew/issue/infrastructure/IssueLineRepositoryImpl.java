package com.gazi.gazi_renew.issue.infrastructure;

import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.domain.IssueLine;
import com.gazi.gazi_renew.issue.infrastructure.entity.IssueEntity;
import com.gazi.gazi_renew.issue.infrastructure.entity.IssueLineEntity;
import com.gazi.gazi_renew.issue.infrastructure.jpa.IssueJpaRepository;
import com.gazi.gazi_renew.issue.infrastructure.jpa.IssueLineJpaRepository;
import com.gazi.gazi_renew.issue.service.port.IssueLineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Repository
@RequiredArgsConstructor
public class IssueLineRepositoryImpl implements IssueLineRepository {
    private final IssueLineJpaRepository issueLineJpaRepository;
    private final IssueJpaRepository issueJpaRepository;
    @Override
    public Page<Issue> findByLineId(Long id, Pageable pageable) {
        List<IssueLine> issueLineList = issueLineJpaRepository.findByLineEntityId(id).stream()
                .map(IssueLineEntity::toModel).collect(Collectors.toList());
        List<Issue> issueList = issueLineList.stream()
                .map(IssueLine::getIssue).collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = (start + pageable.getPageSize()) > issueList.size()? issueList.size() : (start + pageable.getPageSize());

        List<Issue> sortedList = issueList.stream()
                .sorted(Comparator.comparing(Issue::getStartDate).reversed()) // startDate를 기준으로 내림차순 정렬
                .collect(Collectors.toList());

        sortedList = sortedList.subList(start, end);
        Page<Issue> sortedIssues = new PageImpl<>(sortedList, pageable, sortedList.size());

        return sortedIssues;
    }

    @Override
    public void save(IssueLine issueLine) {
        Optional<IssueEntity> byId = issueJpaRepository.findById(issueLine.getIssue().getId());
        issueLineJpaRepository.save(IssueLineEntity.from(issueLine, byId.get()));
    }

    @Override
    public List<IssueLine> findAllByIssue(Issue issue) {
        return issueLineJpaRepository.findByIssueEntityId(issue.getId())
                .stream().map(IssueLineEntity::toModel).collect(Collectors.toList());
    }

    @Override
    public void deleteIssueLineByIssueId(Long issueId) {
        issueLineJpaRepository.deleteByIssueEntityId(issueId);
    }
}
