package com.gazi.gazi_renew.station.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FindRoadRequest {
    // 출발역이름
    private final String strStationName;
    // 출발역호선
    private final String strStationLine;
    // 도착역이름
    private final String endStationName;
    // 도착역호선
    private final String endStationLine;
    @Builder
    public FindRoadRequest(String strStationName, String strStationLine, String endStationName, String endStationLine) {
        this.strStationName = strStationName;
        this.strStationLine = strStationLine;
        this.endStationName = endStationName;
        this.endStationLine = endStationLine;
    }
}
