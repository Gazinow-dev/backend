package com.gazi.gazi_renew.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FindRoadRequest {

    // 출발역이름
    String strSubwayName;
    // 출발역호선
    String strSubwayLine;
    // 도착역이름
    String endSubwayName;
    // 도착역호선
    String endSubwayLine;
}
