package com.gazi.gazi_renew.route.domain;

import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadStationCreate;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
public class MyFindRoadStation {
    private final Long id;
    private final int index; // 정류장 순번
    private final String stationName;
    private final List<Issue> issueList;
    private final Long myFindRoadSubPathId;
    @Builder
    public MyFindRoadStation(Long id, int index, String stationName, List<Issue> issueList, Long myFindRoadSubPathId) {
        this.id = id;
        this.index = index;
        this.stationName = stationName;
        this.issueList = issueList;
        this.myFindRoadSubPathId = myFindRoadSubPathId;
    }

    public static MyFindRoadStation from(MyFindRoadStationCreate myFindRoadStationCreate) {
        return MyFindRoadStation.builder()
                .index(myFindRoadStationCreate.getIndex())
                .stationName(myFindRoadStationCreate.getStationName())
                .build();

    }

    public MyFindRoadStation updateIssueList(List<Issue> issueList) {
        return MyFindRoadStation.builder()
                .index(this.index)
                .stationName(this.stationName)
                .issueList(issueList)
                .build();
    }
}