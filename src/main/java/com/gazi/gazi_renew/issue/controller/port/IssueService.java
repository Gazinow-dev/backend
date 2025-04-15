package com.gazi.gazi_renew.issue.controller.port;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.domain.dto.*;
import com.gazi.gazi_renew.station.domain.Station;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface IssueService {
    // 이슈 조회
    List<IssueStationDetail> getIssue(Long id);


    // 이슈 전체조회
    Page<IssueStationDetail> getIssues(Pageable pageable);
    // 이슈 필터조회
    Page<IssueStationDetail> getLineByIssues(String line, Pageable pageable);

    void updateIssue(IssueUpdate issueUpdate);
    //인기 이슈 조회
    List<IssueStationDetail> getPopularIssues();

    List<Station> findStationsForOtherLines(int startStationCode, int endStationCode);

    void deleteIssue(Long id);

    Issue autoRegisterInternalIssue(InternalIssueCreate internalIssueCreate) throws JsonProcessingException;

    Issue autoRegisterExternalIssue(ExternalIssueCreate externalIssueCreate) throws JsonProcessingException;

    List<IssueStationDetail> getMainIssues();
}
