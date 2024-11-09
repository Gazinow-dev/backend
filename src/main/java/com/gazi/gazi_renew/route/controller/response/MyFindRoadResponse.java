package com.gazi.gazi_renew.route.controller.response;

import com.gazi.gazi_renew.issue.controller.response.IssueResponse;
import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.route.domain.MyFindRoad;
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
    private final List<transitStation> transitStations;
    private final List<MyFindRoadResponse.SubPath> subPaths;


    @Builder
    public MyFindRoadResponse(Long id, int totalTime, String roadName, String lastEndStation, Boolean notification, List<transitStation> transitStations, List<SubPath> subPaths) {
        this.id = id;
        this.totalTime = totalTime;
        this.roadName = roadName;
        this.lastEndStation = lastEndStation;
        this.notification = notification;
        this.transitStations = transitStations;
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
    public static class transitStation {
        private final String stationName;
        private final String line;
        @Builder
        public transitStation(String stationName, String line) {
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
        private List<IssueResponse.IssueSummaryDto> issueSummary; // 호선에서 발생한 이슈
        @Builder
        public Lane(String name, int stationCode, String startName, String endName, boolean direct, List<IssueResponse.IssueSummaryDto> issueSummary) {
            this.name = name;
            this.stationCode = stationCode;
            this.startName = startName;
            this.endName = endName;
            this.direct = direct;
            this.issueSummary = issueSummary;
        }

        public void setIssueSummary(List<IssueResponse.IssueSummaryDto> issueSummary) {
            this.issueSummary = issueSummary;
        }
    }

    @Getter
    static public class Station {
        private final int index; // 정류장 순번
        private final String stationName;
        private final int stationCode;
        private final List<IssueResponse.IssueSummaryDto> issueSummary; // 역에서 발생한 이슈
        @Builder
        public Station(int index, String stationName, int stationCode, List<IssueResponse.IssueSummaryDto> issueSummary) {
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
            for(MyFindRoad.SubPath subPath : myFindRoad.getSubPaths()){
                SubPath subPathResponse = SubPath.builder()
                        .way(subPath.getWay())
                        .door(subPath.getDoor())
                        .trafficType(subPath.getTrafficType())
                        .stationCount(subPath.getStationCount())
                        .sectionTime(subPath.getSectionTime())
                        .distance(subPath.getDistance())
                        .build();

                if(subPath.getTrafficType() == 1) {
                    List<MyFindRoadResponse.Lane> lanes = new ArrayList<>();
                    for (MyFindRoad.Lane lane : subPath.getLanes()) {
                        // response로 가공
                        String lineName = lane.getName();

                        boolean isDirect = false;
                        if (lineName.contains("(급행)")) {
                            isDirect = true;
                        }
                        Lane buildLane = Lane.builder()
                                .name(lane.getName())
                                .startName(lane.getStartName())
                                .endName(lane.getEndName())
                                .stationCode(lane.getStationCode())
                                .direct(isDirect)
                                .build();

                        lanes.add(buildLane);
                    }
                    List<IssueResponse.IssueSummaryDto> issueDtoList = new ArrayList<>();
                    List<MyFindRoadResponse.Station> stations = new ArrayList<>();

                    for (MyFindRoad.Station myFindRoadStation : subPath.getStations()) {
                        List<Issue> issueList = myFindRoadStation.getIssueList();
                        List<Issue> activeIssue = new ArrayList<>();
                        // activeIssues에 issues 중에서 issue.getExpireDate값이 현재시간보다 앞서는 값만 받도록 설계
                        LocalDateTime currentDateTime = LocalDateTime.now(); // 현재 시간

                        for (Issue issue : issueList) {
                            if (issue.getExpireDate() != null && issue.getExpireDate().isAfter(currentDateTime)) {
                                activeIssue.add(issue);
                            }
                        }
                        List<IssueResponse.IssueSummaryDto> issueSummaryDtoList =IssueResponse.IssueSummaryDto.getIssueSummaryDto(activeIssue);
                        MyFindRoadResponse.Station station = MyFindRoadResponse.Station.builder()
                                .stationName(myFindRoadStation.getStationName())
                                .index(myFindRoadStation.getIndex())
                                .issueSummary(IssueResponse.IssueSummaryDto.getIssueSummaryDto(activeIssue))
                                .build();
                        stations.add(station);

                        issueDtoList.addAll(issueSummaryDtoList);
                    };
                    subPathResponse.setLanes(lanes);

                    // 호선 이슈리스트 추가 (내 길찾기 역중에서만)
                    if(!subPathResponse.getLanes().isEmpty()) {
                        subPathResponse.getLanes().get(0).setIssueSummary(IssueResponse.IssueSummaryDto.getIssueSummaryDtoByLine(issueDtoList));
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
        for (MyFindRoad.SubPath subPath : myFindRoad.getSubPaths()) {
            SubPath.SubPathBuilder subPathBuilder = SubPath.builder()
                    .way(subPath.getWay())
                    .door(subPath.getDoor())
                    .trafficType(subPath.getTrafficType())
                    .stationCount(subPath.getStationCount())
                    .sectionTime(subPath.getSectionTime())
                    .distance(subPath.getDistance());

            if (subPath.getTrafficType() == 1) {
                List<Lane> lanes = new ArrayList<>();
                for (MyFindRoad.Lane lane : subPath.getLanes()) {
                    boolean isDirect = lane.getName().contains("(급행)");
                    Lane buildLane = Lane.builder()
                            .name(lane.getName())
                            .startName(lane.getStartName())
                            .endName(lane.getEndName())
                            .stationCode(lane.getStationCode())
                            .direct(isDirect)
                            .build();
                    lanes.add(buildLane);
                }

                List<IssueResponse.IssueSummaryDto> issueDtoList = new ArrayList<>();
                List<Station> stations = new ArrayList<>();
                for (MyFindRoad.Station myFindRoadStation : subPath.getStations()) {
                    List<Issue> issueList = myFindRoadStation.getIssueList();
                    List<Issue> activeIssues = new ArrayList<>();
                    LocalDateTime currentDateTime = LocalDateTime.now();
                    for (Issue issue : issueList) {
                        if (issue.getExpireDate() != null && issue.getExpireDate().isAfter(currentDateTime)) {
                            activeIssues.add(issue);
                        }
                    }
                    List<IssueResponse.IssueSummaryDto> issueSummaryDtoList = IssueResponse.IssueSummaryDto.getIssueSummaryDto(activeIssues);
                    Station station = Station.builder()
                            .stationName(myFindRoadStation.getStationName())
                            .index(myFindRoadStation.getIndex())
                            .issueSummary(issueSummaryDtoList)
                            .build();
                    stations.add(station);
                    issueDtoList.addAll(issueSummaryDtoList);
                }

                if (!lanes.isEmpty()) {
                    lanes.get(0).setIssueSummary(IssueResponse.IssueSummaryDto.getIssueSummaryDtoByLine(issueDtoList));
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