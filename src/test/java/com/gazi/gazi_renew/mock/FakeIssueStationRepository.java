package com.gazi.gazi_renew.mock;

import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.domain.IssueStation;
import com.gazi.gazi_renew.issue.service.port.IssueStationRepository;

import java.util.List;

public class FakeIssueStationRepository implements IssueStationRepository {
    @Override
    public List<IssueStation> findAllByStationId(Long stationId) {
        return null;
    }

    @Override
    public void save(IssueStation issueStation) {

    }

    @Override
    public List<IssueStation> findAllByIssue(Issue issue) {
        return null;
    }
}
