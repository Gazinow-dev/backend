package com.gazi.gazi_renew.route.domain.dto;

import com.gazi.gazi_renew.issue.controller.response.IssueResponse;
import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.domain.IssueSummary;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class MyFindRoadStation {
    private final int index; // 정류장 순번
    private final String stationName;
    private final List<Issue> issueList;
    @Builder
    public MyFindRoadStation(int index, String stationName, List<Issue> issueList) {
        this.index = index;
        this.stationName = stationName;
        this.issueList = issueList;
    }
}