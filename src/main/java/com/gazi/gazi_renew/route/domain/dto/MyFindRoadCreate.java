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
    private final Integer walkingTimeFromStartStation; // 출발역까지 도보 시간 (분)
    private final Integer walkingTimeToEndStation;     // 도착역에서 도보 시간 (분)
    private final List<MyFindRoadSubPathCreate> subPaths;
    @Builder
    public MyFindRoadCreate(String roadName, int totalTime, int stationTransitCount, String firstStartStation, String lastEndStation, Integer walkingTimeFromStartStation, Integer walkingTimeToEndStation, List<MyFindRoadSubPathCreate> subPaths) {
        this.roadName = roadName;
        this.totalTime = totalTime;
        this.stationTransitCount = stationTransitCount;
        this.firstStartStation = firstStartStation;
        this.lastEndStation = lastEndStation;
        this.walkingTimeFromStartStation = walkingTimeFromStartStation;
        this.walkingTimeToEndStation = walkingTimeToEndStation;
        this.subPaths = subPaths;
    }

}