package com.gazi.gazi_renew.station.domain;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FindRoadRequest {

    // 출발역이름
    String strStationName;
    // 출발역호선
    String strStationLine;
    // 도착역이름
    String endStationName;
    // 도착역호선
    String endStationLine;
}
