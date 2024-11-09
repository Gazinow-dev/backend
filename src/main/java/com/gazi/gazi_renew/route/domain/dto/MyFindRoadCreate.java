package com.gazi.gazi_renew.route.domain.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class MyFindRoadCreate {
    private final String roadName;
    private final int totalTime; // 총소요시간
    private final int stationTransitCount;
    private final String firstStartStation;
    private final String lastEndStation;
    private final List<MyFindRoadSubPath> myFindRoadSubPaths;
    @Builder
    public MyFindRoadCreate(String roadName, int totalTime, int stationTransitCount, String firstStartStation, String lastEndStation, List<MyFindRoadSubPath> myFindRoadSubPaths) {
        this.roadName = roadName;
        this.totalTime = totalTime;
        this.stationTransitCount = stationTransitCount;
        this.firstStartStation = firstStartStation;
        this.lastEndStation = lastEndStation;
        this.myFindRoadSubPaths = myFindRoadSubPaths;
    }

}