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
    int totalTime; // 총소요시간
    private String roadName;
    private String lastEndStation;
    private List<transitStation> transitStations;
    private ArrayList<MyFindRoadResponse.SubPath> subPaths;

    @Getter
    @Setter
    @Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    static public class SubPath {
        private int trafficType; //이동수단 종류
        private double distance; //이동거리
        private int sectionTime; //이동 소요 시간
        private int stationCount; // 정차하는 역 개수
        private String way; //  방면
        private String door; //
        private ArrayList<MyFindRoadResponse.Lane> lanes; //
        private ArrayList<MyFindRoadResponse.Station> stations;
    }

    @Getter
    @Setter
    @Builder
    public static class transitStation {
        private String stationName;
        private String line;
    }

    @Getter
    @Setter
    @Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    static public class Lane {
        private String name; // 노선명
        private int stationCode; //노선코드 ex:) 2
        private String startName; //승차 정류장
        private String endName; // 하차 정류장
        private boolean direct; // 급행여부
        private List<IssueResponse.IssueSummaryDto> issueSummary; // 호선에서 발생한 이슈
    }

    @Getter
    @Setter
    @Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    static public class Station {
        private int index; // 정류장 순번
        private String stationName;
        private int stationCode;
        List<IssueResponse.IssueSummaryDto> issueSummary; // 역에서 발생한 이슈
    }
}