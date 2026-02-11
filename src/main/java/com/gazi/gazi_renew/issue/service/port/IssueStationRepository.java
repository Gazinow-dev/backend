package com.gazi.gazi_renew.issue.service.port;

import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.domain.IssueStation;

import java.util.List;

public interface IssueStationRepository {

    List<IssueStation> findAllByStationId(Long stationId);

    void save(IssueStation issueStation);

    List<IssueStation> findAllByIssue(Long issueId);

    void deleteIssueStationByIssueId(Long issueId);
}
