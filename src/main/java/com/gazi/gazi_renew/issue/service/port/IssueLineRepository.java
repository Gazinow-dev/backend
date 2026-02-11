package com.gazi.gazi_renew.issue.service.port;

import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.domain.IssueLine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IssueLineRepository {

    Page<Issue> findByLineId(Long id, Pageable pageable);

    void save(IssueLine issueLine);

    List<IssueLine> findAllByIssue(Long issueId);

    void deleteIssueLineByIssueId(Long issueId);
}
