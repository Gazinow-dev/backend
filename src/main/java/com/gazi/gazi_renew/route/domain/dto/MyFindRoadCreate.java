package com.gazi.gazi_renew.route.domain.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class MyFindRoadCreate {
    private final String roadName;
    private final int totalTime; // 총소요시간
    private final int stationTransitCount;
    private final String firstStartStation;
    private final String lastEndStation;
    private final List<SubPath> subPaths;
    @Builder
    public MyFindRoadCreate(String roadName, int totalTime, int stationTransitCount, String firstStartStation, String lastEndStation, List<SubPath> subPaths) {
        this.roadName = roadName;
        this.totalTime = totalTime;
        this.stationTransitCount = stationTransitCount;
        this.firstStartStation = firstStartStation;
        this.lastEndStation = lastEndStation;
        this.subPaths = subPaths;
    }

}