package com.gazi.gazi_renew.issue.service.port;

import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.infrastructure.IssueEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IssueRepository {
    boolean existsByCrawlingNo(String crawlingNo);
    List<Issue> findTopIssuesByLikesCount(int likesCount, Pageable pageable);

    Issue save(Issue issue);

    Page<Issue> findAll(Pageable pageable);

    Optional<Issue> findById(Long id);

    void updateContent(Issue issue);
}
