package com.gazi.gazi_renew.dto;

import lombok.Getter;

@Getter
public class FindRoadResponse {
    String searchTime; // 검색 시간
    int totalTime; // 총 소요시간
    int subwayTransitCount; // 지하철 환승카운트
}
