package com.gazi.gazi_renew.mock;

import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.domain.IssueLine;
import com.gazi.gazi_renew.issue.service.port.IssueLineRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class FakeIssueLineRepository implements IssueLineRepository {
    @Override
    public Page<Issue> findByLineId(Long id, Pageable pageable) {
        return null;
    }

    @Override
    public void save(IssueLine issueLine) {

    }

    @Override
    public List<IssueLine> findAllByIssue(Issue issue) {
        return null;
    }
}
