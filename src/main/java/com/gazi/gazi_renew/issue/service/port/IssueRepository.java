package com.gazi.gazi_renew.issue.service.port;

import com.gazi.gazi_renew.issue.domain.Issue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IssueRepository {
    boolean existsByCrawlingNo(String crawlingNo);
    List<Issue> findTopIssuesByLikesCount(int likesCount, Pageable pageable);

    Issue save(Issue issue);

    Page<Issue> findAll(Pageable pageable);

    Optional<Issue> findById(Long id);

    void updateIssue(Issue issue);

    void updateLikeCount(Issue issue);

    void flush();

    void deleteIssue(Long id);
}
