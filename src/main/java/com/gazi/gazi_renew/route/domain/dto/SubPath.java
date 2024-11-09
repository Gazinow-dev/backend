package com.gazi.gazi_renew.route.domain.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class SubPath {
    private final int trafficType; //이동수단 종류
    private final double distance; //이동거리
    private final int sectionTime; //이동 소요 시간
    private final int stationCount; // 정차하는 역 개수
    private final  String way; //  방면
    private final  String door; // 문
    private final List<MyFindRoadLane> lanes; //
    private final List<MyFindRoadStation> stations;
    @Builder
    public SubPath(int trafficType, double distance, int sectionTime, int stationCount, String way, String door, List<MyFindRoadLane> lanes, List<MyFindRoadStation> stations) {
        this.trafficType = trafficType;
        this.distance = distance;
        this.sectionTime = sectionTime;
        this.stationCount = stationCount;
        this.way = way;
        this.door = door;
        this.lanes = lanes;
        this.stations = stations;
    }
}
