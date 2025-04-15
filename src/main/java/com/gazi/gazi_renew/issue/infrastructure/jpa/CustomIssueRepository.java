package com.gazi.gazi_renew.issue.infrastructure.jpa;

import com.gazi.gazi_renew.issue.domain.dto.IssueStationDetail;
import com.gazi.gazi_renew.issue.infrastructure.entity.IssueEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CustomIssueRepository {
    List<IssueStationDetail> findTopIssuesByLikesCount();
    Page<IssueStationDetail> findAllByOrderByStartDateDesc(Pageable pageable);

    Optional<IssueEntity> findByIssueKey(String issueKey);

    List<IssueStationDetail> getIssueById(Long id);

    Page<IssueStationDetail> getIssueByLineName(String lineName, Pageable pageable);

    List<IssueStationDetail> findTodayOrActiveIssues();
    List<IssueStationDetail> findIssueOrderByStartDate();
}
