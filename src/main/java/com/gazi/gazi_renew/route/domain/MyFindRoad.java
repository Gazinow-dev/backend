package com.gazi.gazi_renew.route.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class MyFindRoad {
    private final String roadName;
    private final int totalTime; // 총소요시간
    private final int stationTransitCount;
    private final String firstStartStation;
    private final String lastEndStation;
    private final List<SubPath> subPaths;
    @Builder
    public MyFindRoad(String roadName, int totalTime, int stationTransitCount, String firstStartStation, String lastEndStation, List<SubPath> subPaths) {
        this.roadName = roadName;
        this.totalTime = totalTime;
        this.stationTransitCount = stationTransitCount;
        this.firstStartStation = firstStartStation;
        this.lastEndStation = lastEndStation;
        this.subPaths = subPaths;
    }

    @Getter
    static public class SubPath {
        private final int trafficType; //이동수단 종류
        private final double distance; //이동거리
        private final int sectionTime; //이동 소요 시간
        private final int stationCount; // 정차하는 역 개수
        private final  String way; //  방면
        private final  String door; // 문
        private final List<Lane> lanes; //
        private final List<Station> stations;
        @Builder
        public SubPath(int trafficType, double distance, int sectionTime, int stationCount, String way, String door, List<Lane> lanes, List<Station> stations) {
            this.trafficType = trafficType;
            this.distance = distance;
            this.sectionTime = sectionTime;
            this.stationCount = stationCount;
            this.way = way;
            this.door = door;
            this.lanes = lanes;
            this.stations = stations;
        }
    }

    @Getter
    static public class Lane {
        private final String name; // 노선명
        private final int stationCode; //노선코드 ex:) 2
        private final String startName; //승차 정류장
        private final String endName; // 하차 정류장
        @Builder
        public Lane(String name, int stationCode, String startName, String endName) {
            this.name = name;
            this.stationCode = stationCode;
            this.startName = startName;
            this.endName = endName;
        }
    }

    @Getter
    static public class Station {
        private final int index; // 정류장 순번
        private final String stationName;
        private final int StationCode;

        public Station(int index, String stationName, int stationCode) {
            this.index = index;
            this.stationName = stationName;
            StationCode = stationCode;
        }
    }

}
