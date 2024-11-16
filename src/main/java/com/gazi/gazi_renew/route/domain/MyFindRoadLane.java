package com.gazi.gazi_renew.route.domain;

import com.gazi.gazi_renew.route.domain.dto.MyFindRoadLaneCreate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MyFindRoadLane {
    private final Long id;
    private final String name; // 노선명
    private final int stationCode; //노선코드 ex:) 2
    private final Long myFindRoadSubPathId;
    @Builder
    public MyFindRoadLane(Long id, String name, int stationCode, Long myFindRoadSubPathId) {
        this.id = id;
        this.name = name;
        this.stationCode = stationCode;
        this.myFindRoadSubPathId = myFindRoadSubPathId;
    }

    public static MyFindRoadLane from(MyFindRoadLaneCreate myFindRoadLaneCreate) {
        return MyFindRoadLane.builder()
                .name(myFindRoadLaneCreate.getName())
                .stationCode(myFindRoadLaneCreate.getStationCode())
                .build();
    }
}