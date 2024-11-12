package com.gazi.gazi_renew.route.domain;

import com.gazi.gazi_renew.route.domain.dto.MyFindRoadSubPathCreate;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class MyFindRoadSubPath {
    private final Long id;
    private final int trafficType; //이동수단 종류
    private final double distance; //이동거리
    private final int sectionTime; //이동 소요 시간
    private final int stationCount; // 정차하는 역 개수
    private final  String way; //  방면
    private final  String door; // 문
    private final List<MyFindRoadLane> lanes; //
    private final List<MyFindRoadStation> stations;
    @Builder
    public MyFindRoadSubPath(Long id, int trafficType, double distance, int sectionTime, int stationCount, String way, String door, List<MyFindRoadLane> lanes, List<MyFindRoadStation> stations) {
        this.id = id;
        this.trafficType = trafficType;
        this.distance = distance;
        this.sectionTime = sectionTime;
        this.stationCount = stationCount;
        this.way = way;
        this.door = door;
        this.lanes = lanes;
        this.stations = stations;
    }

    public static MyFindRoadSubPath from(MyFindRoadSubPathCreate myFindRoadSubPathCreate) {
        return MyFindRoadSubPath.builder()
                .trafficType(myFindRoadSubPathCreate.getTrafficType())
                .distance(myFindRoadSubPathCreate.getDistance())
                .sectionTime(myFindRoadSubPathCreate.getSectionTime())
                .stationCount(myFindRoadSubPathCreate.getStationCount())
                .stationCount(myFindRoadSubPathCreate.getStationCount())
                .way(myFindRoadSubPathCreate.getWay())
                .door(myFindRoadSubPathCreate.getDoor())
                .lanes(myFindRoadSubPathCreate.getLanes().stream()
                        .map(MyFindRoadLane::from).collect(Collectors.toList())
                )
                .stations(myFindRoadSubPathCreate.getStations().stream()
                        .map(MyFindRoadStation::from).collect(Collectors.toList()))
                .build();
    }
}
