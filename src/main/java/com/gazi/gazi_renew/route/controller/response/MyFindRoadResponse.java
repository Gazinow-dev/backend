package com.gazi.gazi_renew.route.controller.response;

import com.gazi.gazi_renew.issue.controller.response.IssueResponse;
import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.domain.IssueSummary;
import com.gazi.gazi_renew.route.domain.MyFindRoad;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadLane;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadStation;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadSubPath;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class MyFindRoadResponse {
    private final Long id;
    private final int totalTime; // 총소요시간
    private final String roadName;
    private final String lastEndStation;
    private final Boolean notification;
    private final List<TransitStation> transitStationList;
    private final List<MyFindRoadResponse.SubPath> subPaths;


    @Builder
    public MyFindRoadResponse(Long id, int totalTime, String roadName, String lastEndStation, Boolean notification, List<TransitStation> transitStationList, List<SubPath> subPaths) {
        this.id = id;
        this.totalTime = totalTime;
        this.roadName = roadName;
        this.lastEndStation = lastEndStation;
        this.notification = notification;
        this.transitStationList = transitStationList;
        this.subPaths = subPaths;
    }

    @Getter
    static public class SubPath {
        private final int trafficType; //이동수단 종류
        private final double distance; //이동거리
        private final int sectionTime; //이동 소요 시간
        private final int stationCount; // 정차하는 역 개수
        private final String way; //  방면
        private final String door; //
        private List<MyFindRoadResponse.Lane> lanes; //
        private List<MyFindRoadResponse.Station> stations;

        public void setStations(List<Station> stations) {
            this.stations = stations;
        }

        public void setLanes(List<Lane> lanes) {
            this.lanes = lanes;
        }

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
    public static class TransitStation {
        private final String stationName;
        private final String line;
        @Builder
        public TransitStation(String stationName, String line) {
            this.stationName = stationName;
            this.line = line;
        }
    }

    @Getter
    static public class Lane {
        private final String name; // 노선명
        private final int stationCode; //노선코드 ex:) 2
        private final String startName; //승차 정류장
        private final String endName; // 하차 정류장
        private final boolean direct; // 급행여부
        private List<IssueSummary> issueSummary; // 호선에서 발생한 이슈
        @Builder
        public Lane(String name, int stationCode, String startName, String endName, boolean direct, List<IssueSummary> issueSummary) {
            this.name = name;
            this.stationCode = stationCode;
            this.startName = startName;
            this.endName = endName;
            this.direct = direct;
            this.issueSummary = issueSummary;
        }

        public void setIssueSummary(List<IssueSummary> issueSummary) {
            this.issueSummary = issueSummary;
        }
    }

    @Getter
    static public class Station {
        private final int index; // 정류장 순번
        private final String stationName;
        private final int stationCode;
        private final List<IssueSummary> issueSummary; // 역에서 발생한 이슈
        @Builder
        public Station(int index, String stationName, int stationCode, List<IssueSummary> issueSummary) {
            this.index = index;
            this.stationName = stationName;
            this.stationCode = stationCode;
            this.issueSummary = issueSummary;
        }
    }
    public static List<MyFindRoadResponse> fromList(List<MyFindRoad> myFindRoadList) {
        List<MyFindRoadResponse> myFindRoadResponses = new ArrayList<>();
        for (MyFindRoad myFindRoad : myFindRoadList) {
            //서브패스를 찾는다.
            List<SubPath> subPaths = new ArrayList<>();
            // subpathID로 lane과 station을 찾는다.
            for(MyFindRoadSubPath myFindRoadSubPath : myFindRoad.getMyFindRoadSubPaths()){
                SubPath subPathResponse = SubPath.builder()
                        .way(myFindRoadSubPath.getWay())
                        .door(myFindRoadSubPath.getDoor())
                        .trafficType(myFindRoadSubPath.getTrafficType())
                        .stationCount(myFindRoadSubPath.getStationCount())
                        .sectionTime(myFindRoadSubPath.getSectionTime())
                        .distance(myFindRoadSubPath.getDistance())
                        .build();

                if(myFindRoadSubPath.getTrafficType() == 1) {
                    List<MyFindRoadResponse.Lane> lanes = new ArrayList<>();
                    for (MyFindRoadLane myFindRoadLane : myFindRoadSubPath.getLanes()) {
                        // response로 가공
                        String lineName = myFindRoadLane.getName();

                        boolean isDirect = false;
                        if (lineName.contains("(급행)")) {
                            isDirect = true;
                        }
                        Lane buildLane = Lane.builder()
                                .name(myFindRoadLane.getName())
                                .startName(myFindRoadLane.getStartName())
                                .endName(myFindRoadLane.getEndName())
                                .stationCode(myFindRoadLane.getStationCode())
                                .direct(isDirect)
                                .build();

                        lanes.add(buildLane);
                    }
                    List<IssueSummary> issueDtoList = new ArrayList<>();
                    List<MyFindRoadResponse.Station> stations = new ArrayList<>();

                    for (MyFindRoadStation myFindRoadStation : myFindRoadSubPath.getStations()) {
                        List<Issue> activeIssue = new ArrayList<>();
                        List<Issue> issueList = myFindRoadStation.getIssueList();
                        // activeIssues에 issues 중에서 issue.getExpireDate값이 현재시간보다 앞서는 값만 받도록 설계
                        LocalDateTime currentDateTime = LocalDateTime.now(); // 현재 시간

                        for (Issue issue : issueList) {
                            if (issue.getExpireDate() != null && issue.getExpireDate().isAfter(currentDateTime)) {
                                activeIssue.add(issue);
                            }
                        }
                        List<IssueSummary> issueSummaryDtoList =IssueSummary.getIssueSummaryDto(activeIssue);
                        MyFindRoadResponse.Station station = MyFindRoadResponse.Station.builder()
                                .stationName(myFindRoadStation.getStationName())
                                .index(myFindRoadStation.getIndex())
                                .issueSummary(IssueSummary.getIssueSummaryDto(activeIssue))
                                .build();
                        stations.add(station);

                        issueDtoList.addAll(issueSummaryDtoList);
                    };
                    subPathResponse.setLanes(lanes);

                    // 호선 이슈리스트 추가 (내 길찾기 역중에서만)
                    if(!subPathResponse.getLanes().isEmpty()) {
                        subPathResponse.getLanes().get(0).setIssueSummary(IssueSummary.getIssueSummaryDtoByLine(issueDtoList));
                    }
                    subPathResponse.setStations(stations);
                }
                subPaths.add(subPathResponse);
            }
            MyFindRoadResponse myFindRoadResponse = MyFindRoadResponse.builder()
                    .id(myFindRoad.getId())
                    .roadName(myFindRoad.getRoadName())
                    .lastEndStation(myFindRoad.getLastEndStation())
                    .notification(myFindRoad.getNotification())
                    .totalTime(myFindRoad.getTotalTime())
                    .subPaths(subPaths)
                    .build();
            myFindRoadResponses.add(myFindRoadResponse);
        }
        return myFindRoadResponses;
    }
    public static MyFindRoadResponse from(MyFindRoad myFindRoad) {
        List<SubPath> subPaths = new ArrayList<>();
        for (MyFindRoadSubPath myFindRoadSubPath : myFindRoad.getMyFindRoadSubPaths()) {
            SubPath.SubPathBuilder subPathBuilder = SubPath.builder()
                    .way(myFindRoadSubPath.getWay())
                    .door(myFindRoadSubPath.getDoor())
                    .trafficType(myFindRoadSubPath.getTrafficType())
                    .stationCount(myFindRoadSubPath.getStationCount())
                    .sectionTime(myFindRoadSubPath.getSectionTime())
                    .distance(myFindRoadSubPath.getDistance());

            if (myFindRoadSubPath.getTrafficType() == 1) {
                List<Lane> lanes = new ArrayList<>();
                for (MyFindRoadLane myFindRoadLane : myFindRoadSubPath.getLanes()) {
                    boolean isDirect = myFindRoadLane.getName().contains("(급행)");
                    Lane buildLane = Lane.builder()
                            .name(myFindRoadLane.getName())
                            .startName(myFindRoadLane.getStartName())
                            .endName(myFindRoadLane.getEndName())
                            .stationCode(myFindRoadLane.getStationCode())
                            .direct(isDirect)
                            .build();
                    lanes.add(buildLane);
                }

                List<IssueSummary> issueDtoList = new ArrayList<>();
                List<Station> stations = new ArrayList<>();
                for (MyFindRoadStation myFindRoadStation : myFindRoadSubPath.getStations()) {
                    List<Issue> issueList = myFindRoadStation.getIssueList();
                    List<Issue> activeIssues = new ArrayList<>();
                    LocalDateTime currentDateTime = LocalDateTime.now();
                    for (Issue issue : issueList) {
                        if (issue.getExpireDate() != null && issue.getExpireDate().isAfter(currentDateTime)) {
                            activeIssues.add(issue);
                        }
                    }
                    List<IssueSummary> issueSummaryDtoList = IssueSummary.getIssueSummaryDto(activeIssues);
                    Station station = Station.builder()
                            .stationName(myFindRoadStation.getStationName())
                            .index(myFindRoadStation.getIndex())
                            .issueSummary(issueSummaryDtoList)
                            .build();
                    stations.add(station);
                    issueDtoList.addAll(issueSummaryDtoList);
                }

                if (!lanes.isEmpty()) {
                    lanes.get(0).setIssueSummary(IssueSummary.getIssueSummaryDtoByLine(issueDtoList));
                }
                subPathBuilder.lanes(lanes);
                subPathBuilder.stations(stations);
            }
            subPaths.add(subPathBuilder.build());
        }

        return MyFindRoadResponse.builder()
                .id(myFindRoad.getId())
                .roadName(myFindRoad.getRoadName())
                .lastEndStation(myFindRoad.getLastEndStation())
                .notification(myFindRoad.getNotification())
                .totalTime(myFindRoad.getTotalTime())
                .subPaths(subPaths)
                .build();
    }
}