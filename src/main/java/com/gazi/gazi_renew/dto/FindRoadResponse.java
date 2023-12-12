package com.gazi.gazi_renew.dto;

import lombok.Getter;

import java.util.ArrayList;

@Getter
public class FindRoadResponse {
    ArrayList<Path> paths;
    int subwayCount; //지하철 경로 개수

    class Path{
        int totalTime; // 총소요시간
        int subwayTransitCount;
        String firstStartStation;
        String lastEndStation;
        ArrayList<subPath> subPaths;
    }

    class subPath{
        int trafficType; //이동수단 종류
        double distance; //이동거리
        int sectionTime; //이동 소요 시간
        int stationCount; // 정차하는 역 개수
        ArrayList<lane> lanes; //

    }
    class lane{
        String name; // 노선명
        int subwayCode; //노선코드 ex:) 2
        String startName; //승차 정류장
        String endName; // 하차 정류장
        ArrayList<subway> subways;
    }

    class subway{
        int index; // 정류장 순번
        String stationName;
    }

}
