package com.gazi.gazi_renew.issue.infrastructure;

import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.domain.dto.IssueStationDetail;
import com.gazi.gazi_renew.issue.infrastructure.entity.IssueEntity;
import com.gazi.gazi_renew.issue.infrastructure.jpa.CustomIssueRepository;
import com.gazi.gazi_renew.issue.infrastructure.jpa.IssueJpaRepository;
import com.gazi.gazi_renew.issue.service.port.IssueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class IssueRepositoryImpl implements IssueRepository {
    private final IssueJpaRepository issueJpaRepository;
    private final CustomIssueRepository customIssueRepository;

    @Override
    public boolean existsByCrawlingNo(String crawlingNo) {
        return issueJpaRepository.existsByCrawlingNo(crawlingNo);
    }

    @Override
    public List<IssueStationDetail> findTopIssuesByLikesCount(Pageable pageable) {
        return customIssueRepository.findTopIssuesByLikesCount(pageable);
    }

    @Override
    public List<IssueStationDetail> findTodayOrActiveIssues() {
        return customIssueRepository.findTodayOrActiveIssues();
    }

    @Override
    public Page<IssueStationDetail> getIssueByLineName(String lineName, Pageable pageable) {
        return customIssueRepository.getIssueByLineName(lineName, pageable);
    }

    @Override
    public Issue save(Issue issue) {
        return issueJpaRepository.save(IssueEntity.from(issue)).toModel();
    }
    @Override
    public Page<IssueStationDetail> findAll(Pageable pageable) {
        return customIssueRepository.findAllByOrderByStartDateDesc(pageable);

    }

    @Override
    public List<IssueStationDetail> getIssueById(Long id) {
        return customIssueRepository.getIssueById(id);
    }

    @Override
    public void updateIssue(Issue issue) {
        issueJpaRepository.updateIssue(issue.getId(), issue.getTitle(), issue.getContent(), issue.getStartDate(), issue.getExpireDate(), issue.getKeyword());
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
    @Override
    public Optional<Issue> findByIssueKey(String issueKey) {
        return customIssueRepository.findByIssueKey(issueKey).map(IssueEntity::toModel);
    }
    @Override
    public Optional<Issue> findById(Long id) {
        return issueJpaRepository.findById(id).map(IssueEntity::toModel);
    }
}