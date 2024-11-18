package com.gazi.gazi_renew.route.domain;

import com.gazi.gazi_renew.route.domain.dto.MyFindRoadSubPathCreate;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class MyFindRoadSubPath {
    private final Long id;
    private final int trafficType; //이동수단 종류
    private final double distance; //이동거리
    private final int sectionTime; //이동 소요 시간
    private final int stationCount; // 정차하는 역 개수
    private final String way; //  방면
    private final String door; // 문
    private final String name; // 노선명
    private final int stationCode; //노선코드 ex:) 2
    private final MyFindRoad myFindRoad;
    private final List<MyFindRoadStation> stations; // 엔티티에는 저장 안 함
    @Builder
    public MyFindRoadSubPath(Long id, int trafficType, double distance, int sectionTime, int stationCount, String way, String door, String name, int stationCode, MyFindRoad myFindRoad, List<MyFindRoadStation> stations) {
        this.id = id;
        this.trafficType = trafficType;
        this.distance = distance;
        this.sectionTime = sectionTime;
        this.stationCount = stationCount;
        this.way = way;
        this.door = door;
        this.name = name;
        this.stationCode = stationCode;
        this.myFindRoad = myFindRoad;
        this.stations = stations;
    }

    public static MyFindRoadSubPath from(MyFindRoadSubPathCreate myFindRoadSubPathCreate, MyFindRoad myFindRoad) {
        return MyFindRoadSubPath.builder()
                .trafficType(myFindRoadSubPathCreate.getTrafficType())
                .distance(myFindRoadSubPathCreate.getDistance())
                .sectionTime(myFindRoadSubPathCreate.getSectionTime())
                .stationCount(myFindRoadSubPathCreate.getStationCount())
                .stationCount(myFindRoadSubPathCreate.getStationCount())
                .way(myFindRoadSubPathCreate.getWay())
                .door(myFindRoadSubPathCreate.getDoor())
                .name(myFindRoadSubPathCreate.getName())
                .stationCode(myFindRoadSubPathCreate.getStationCode())
                .myFindRoad(myFindRoad)
                .build();
    }

    public MyFindRoadSubPath updateStations(List<MyFindRoadStation> updatedStations) {
        return MyFindRoadSubPath.builder()
                .id(this.id)
                .trafficType(this.trafficType)
                .distance(this.distance)
                .sectionTime(this.sectionTime)
                .stationCount(this.stationCount)
                .way(this.way)
                .door(this.door)
                .name(this.name)
                .stationCode(this.stationCode)
                .myFindRoad(this.myFindRoad)
                .stations(updatedStations)
                .build();
    }

}
