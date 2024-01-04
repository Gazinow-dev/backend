package com.gazi.gazi_renew.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class MyFindRoadRequest {
    ArrayList<Path> paths;
    int subwayCount; //지하철 경로 개수
    String name;

    @Getter
    @Setter
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    static public class Path {
        int totalTime; // 총소요시간
        int subwayTransitCount;
        String firstStartStation;
        String lastEndStation;
        ArrayList<SubPath> subPaths;
    }

    @Getter
    @Setter
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    static public class SubPath {
        int trafficType; //이동수단 종류
        double distance; //이동거리
        int sectionTime; //이동 소요 시간
        int stationCount; // 정차하는 역 개수
        ArrayList<Lane> lanes; //

        ArrayList<Subway> subways;
    }

    @Getter
    @Setter
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    static public class Lane {
        String name; // 노선명
        int subwayCode; //노선코드 ex:) 2
        String startName; //승차 정류장
        String endName; // 하차 정류장

    }

    @Getter
    @Setter
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    static public class Subway {
        int index; // 정류장 순번
        String stationName;
    }

}
