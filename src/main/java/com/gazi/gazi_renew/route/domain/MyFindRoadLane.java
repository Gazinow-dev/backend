package com.gazi.gazi_renew.route.domain;

import com.gazi.gazi_renew.route.domain.dto.MyFindRoadLaneCreate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MyFindRoadLane {
    private final Long id;
    private final String name; // 노선명
    private final int stationCode; //노선코드 ex:) 2
    private final String startName; //승차 정류장
    private final String endName; // 하차 정류장
    private final Long myFindRoadSubPathId;
    @Builder
    public MyFindRoadLane(Long id, String name, int stationCode, String startName, String endName, Long myFindRoadSubPathId) {
        this.id = id;
        this.name = name;
        this.stationCode = stationCode;
        this.startName = startName;
        this.endName = endName;
        this.myFindRoadSubPathId = myFindRoadSubPathId;
    }

    public static MyFindRoadLane from(MyFindRoadLaneCreate myFindRoadLaneCreate) {
        return MyFindRoadLane.builder()
                .name(myFindRoadLaneCreate.getName())
                .stationCode(myFindRoadLaneCreate.getStationCode())
                .startName(myFindRoadLaneCreate.getStartName())
                .endName(myFindRoadLaneCreate.getEndName())
                .build();
    }
}