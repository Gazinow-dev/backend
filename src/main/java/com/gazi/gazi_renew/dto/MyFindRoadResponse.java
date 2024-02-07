package com.gazi.gazi_renew.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class MyFindRoadResponse {
    private Long id;
    private String roadName;
    private String lastEndStation;
    private List<transitStation> transitStations;
    private List<MyFindRoadResponse.issue> issues;
    private ArrayList<MyFindRoadResponse.SubPath> subPaths;

    @Getter
    @Setter
    @Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    static public class SubPath {
        ArrayList<MyFindRoadResponse.Lane> lanes; //
        ArrayList<MyFindRoadResponse.Station> stations;
    }

    @Getter
    @Setter
    @Builder
    public static class transitStation {
        private String stationName;
        private String line;
    }

    // 이슈
    @Getter
    public class issue{
        //어디에서 어디사이에서 발생한 이슈인지
        private String point;
    }

    @Getter
    @Setter
    @Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    static public class Lane {
        String name; // 노선명
        int stationCode; //노선코드 ex:) 2
        String startName; //승차 정류장
        String endName; // 하차 정류장

    }

    @Getter
    @Setter
    @Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    static public class Station {
        int index; // 정류장 순번
        String stationName;
    }
}