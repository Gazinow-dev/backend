package com.gazi.gazi_renew.issue.service.port;

import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.domain.dto.IssueStationDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IssueRepository {
    boolean existsByCrawlingNo(String crawlingNo);
    List<IssueStationDetail> findTopIssuesByLikesCount();

    List<IssueStationDetail> findTodayOrActiveIssues();
    Page<IssueStationDetail> getIssueByLineName(String lineName, Pageable pageable);
    Issue save(Issue issue);

    Page<IssueStationDetail> findAll(Pageable pageable);

    List<IssueStationDetail> getIssueById(Long id);

    void updateIssue(Issue issue);

    void updateLikeCount(Issue issue);

    void flush();

    void deleteIssue(Long id);

    void updateStartDateAndExpireDate(Long id, LocalDateTime startDate, LocalDateTime expireDate);

    Optional<Issue> findByIssueKey(String issueKey);

    Optional<Issue> findById(Long id);

    List<IssueStationDetail> findIssueOrderByStartDate();
}
