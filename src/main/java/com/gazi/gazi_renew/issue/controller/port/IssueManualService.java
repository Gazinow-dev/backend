package com.gazi.gazi_renew.issue.controller.port;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gazi.gazi_renew.issue.domain.dto.IssueCreate;
import com.gazi.gazi_renew.station.domain.Station;

import java.util.List;

public interface IssueManualService {
    List<Station> handleLineTwo(IssueCreate.Station issueStation, int startStationCode, int endStationCode);

    List<Station> handleClockwiseDirection(int startStationCode, int endStationCode);

    List<Station> handleCounterClockwiseDirection(int startStationCode, int endStationCode);

    List<Station> getStationsForCircularRoute(int startStationCode, int endStationCode);
    boolean addIssue(IssueCreate issueCreate) throws JsonProcessingException;
}
