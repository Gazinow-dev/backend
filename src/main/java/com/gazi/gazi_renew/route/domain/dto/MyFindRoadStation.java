package com.gazi.gazi_renew.route.domain.dto;

import com.gazi.gazi_renew.issue.domain.Issue;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class MyFindRoadStation {
    private final int index; // 정류장 순번
    private final String stationName;
    private final int stationCode;
    private final List<Issue> issueList;
    @Builder
    public MyFindRoadStation(int index, String stationName, int stationCode, List<Issue> issueList) {
        this.index = index;
        this.stationName = stationName;
        this.stationCode = stationCode;
        this.issueList = issueList;
    }
}