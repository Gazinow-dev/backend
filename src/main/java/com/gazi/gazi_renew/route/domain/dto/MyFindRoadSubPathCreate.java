package com.gazi.gazi_renew.route.domain.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class MyFindRoadSubPathCreate {
    private final int trafficType; //이동수단 종류
    private final double distance; //이동거리
    private final int sectionTime; //이동 소요 시간
    private final int stationCount; // 정차하는 역 개수
    private final  String way; //  방면
    private final  String door; // 문
    private final String name;
    private final int stationCode;
    private final List<MyFindRoadStationCreate> stations;
    @Builder
    public MyFindRoadSubPathCreate(int trafficType, double distance, int sectionTime, int stationCount, String way, String door, String name, int stationCode, List<MyFindRoadStationCreate> stations) {
        this.trafficType = trafficType;
        this.distance = distance;
        this.sectionTime = sectionTime;
        this.stationCount = stationCount;
        this.way = way;
        this.door = door;
        this.name = name;
        this.stationCode = stationCode;
        this.stations = stations;
    }
}
