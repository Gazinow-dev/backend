package com.gazi.gazi_renew.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.gazi.gazi_renew.domain.MyFindRoadPath;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FindRoadResponse {
    ArrayList<Path> paths;
    int stationCount; //지하철 경로 개수

    @Setter
    @Getter
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    static public class Path {
        int totalTime; // 총소요시간
        int stationTransitCount;
        String firstStartStation;
        String lastEndStation;
        ArrayList<SubPath> subPaths;
        ArrayList<TransitStation> transitStations;
        boolean myPath;
        List<Long> myPathId;
    }

    @Getter
    @Setter
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    static public class SubPath {
        int trafficType; //이동수단 종류
        double distance; //이동거리
        int sectionTime; //이동 소요 시간
        int stationCount; // 정차하는 역 개수

        String way; //  방면 ( 다음역으로 수정)
        String door; //
        ArrayList<Lane> lanes; //
        ArrayList<Station> stations;
    }

    @Getter
    @Setter
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    static public class Lane {
        String name; // 노선명
        int stationCode; //노선코드 ex:) 2
        String startName; //승차 정류장
        String endName; // 하차 정류장

    }
    @Getter
    @Setter
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    @Builder
    static public class TransitStation {
        String stationsName;
        String line;

    }

    @Getter
    @Setter
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    static public class Station {
        int index; // 정류장 순번
        String stationName;
    }


}
