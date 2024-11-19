package com.gazi.gazi_renew.issue.controller.port;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.domain.dto.IssueCreate;
import com.gazi.gazi_renew.issue.domain.dto.IssueStationDetail;
import com.gazi.gazi_renew.issue.domain.dto.IssueUpdate;
import com.gazi.gazi_renew.station.domain.Station;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IssueService {
    // 웹 크롤링

    // 이슈 저장
    boolean addIssue(IssueCreate issueCreate) throws JsonProcessingException;
    // 이슈 조회
    IssueStationDetail getIssue(Long id);

    IssueStationDetail getIssueStationDetail(Issue issue, boolean isLike);

    // 이슈 전체조회
    Page<IssueStationDetail> getIssues(Pageable pageable);
    // 이슈 필터조회
    Page<IssueStationDetail> getLineByIssues(String line, Pageable pageable);

    void updateIssueContent(IssueUpdate issueUpdate);
    //인기 이슈 조회
    List<IssueStationDetail> getPopularIssues();

    List<Station> handleLineTwo(IssueCreate.Station issueStation, int startStationCode, int endStationCode);

    List<Station> handleClockwiseDirection(int startStationCode, int endStationCode);

    List<Station> handleCounterClockwiseDirection(int startStationCode, int endStationCode);

    List<Station> getStationsForCircularRoute(int startStationCode, int endStationCode);

    List<Station> findStationsForOtherLines(int startStationCode, int endStationCode);

}
