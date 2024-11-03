package com.gazi.gazi_renew.station.domain;

import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.infrastructure.IssueEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class Station {
    private final Long id;
    private final String line;
    private final String name;
    private final int stationCode;
    private final double lat;
    private final double lng;
    private final Integer issueStationCode;
    private final List<Issue> issueList;
    @Builder
    public Station(Long id, String line, String name, int stationCode, double lat, double lng, Integer issueStationCode, List<Issue> issueList) {
        this.id = id;
        this.line = line;
        this.name = name;
        this.stationCode = stationCode;
        this.lat = lat;
        this.lng = lng;
        this.issueStationCode = issueStationCode;
        this.issueList = issueList;
    }

    public void addIssue(List<Issue> issueList) {
        Station.builder()
                .id(this.id)
                .line(this.line)
                .name(this.name)
                .stationCode(this.stationCode)
                .lat(this.lat)
                .lng(this.lng)
                .issueStationCode(this.issueStationCode)
                .issueList(issueList)
                .build();
    }
}














