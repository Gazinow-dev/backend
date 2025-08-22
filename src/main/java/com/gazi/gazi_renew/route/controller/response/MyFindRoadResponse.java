package com.gazi.gazi_renew.route.controller.response;

import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.domain.dto.IssueSummary;
import com.gazi.gazi_renew.route.domain.MyFindRoad;
import com.gazi.gazi_renew.route.domain.MyFindRoadStation;
import com.gazi.gazi_renew.route.domain.MyFindRoadSubPath;
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
        private final String name;
        private final int stationCode;
        private final boolean direct; // 급행여부
        private final List<IssueSummary> issueSummary; // 호선에서 발생한 이슈
        private final List<MyFindRoadResponse.Station> stations;
        @Builder
        public SubPath(int trafficType, double distance, int sectionTime, int stationCount, String way, String door, String name, int stationCode, boolean direct, List<IssueSummary> issueSummary, List<Station> stations) {
            this.trafficType = trafficType;
            this.distance = distance;
            this.sectionTime = sectionTime;
            this.stationCount = stationCount;
            this.way = way;
            this.door = door;
            this.name = name;
            this.stationCode = stationCode;
            this.direct = direct;
            this.issueSummary = issueSummary;
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
        LocalDateTime currentDateTime = LocalDateTime.now();

        for (MyFindRoad myFindRoad : myFindRoadList) {
            List<SubPath> subPaths = new ArrayList<>();

            for (MyFindRoadSubPath myFindRoadSubPath : myFindRoad.getSubPaths()) {
                String lineName = myFindRoadSubPath.getName();
                boolean isDirect = lineName != null && lineName.contains("(급행)");

                SubPath subPathResponse = SubPath.builder().build();

                if (myFindRoadSubPath.getTrafficType() == 1) {
                    List<IssueSummary> issueDtoList = new ArrayList<>();
                    List<MyFindRoadResponse.Station> stations = new ArrayList<>();

                    for (MyFindRoadStation myFindRoadStation : myFindRoadSubPath.getStations()) {
                        // 횸 화면 이슈 필터링
                        List<Issue> activeIssue = filterIssuesByUserSetting(
                                myFindRoadStation.getIssueList(),
                                myFindRoad.getMemberNextDayIssueNotificationEnabled(),
                                currentDateTime
                        );

                        List<IssueSummary> issueSummaryDtoList = IssueSummary.getIssueSummaryDto(activeIssue);
                        MyFindRoadResponse.Station station = MyFindRoadResponse.Station.builder()
                                .stationName(myFindRoadStation.getStationName())
                                .index(myFindRoadStation.getIndex())
                                .issueSummary(issueSummaryDtoList)
                                .build();

                        stations.add(station);
                        issueDtoList.addAll(issueSummaryDtoList);
                    }

                    subPathResponse = SubPath.builder()
                            .trafficType(myFindRoadSubPath.getTrafficType())
                            .way(myFindRoadSubPath.getWay())
                            .door(myFindRoadSubPath.getDoor())
                            .stationCount(myFindRoadSubPath.getStationCount())
                            .sectionTime(myFindRoadSubPath.getSectionTime())
                            .distance(myFindRoadSubPath.getDistance())
                            .name(myFindRoadSubPath.getName())
                            .stationCode(myFindRoadSubPath.getStationCode())
                            .direct(isDirect)
                            .issueSummary(IssueSummary.getIssueSummaryDtoByLine(issueDtoList))
                            .stations(stations)
                            .build();
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
        for (MyFindRoadSubPath myFindRoadSubPath : myFindRoad.getSubPaths()) {
            boolean isDirect = myFindRoadSubPath.getName().contains("(급행)");
            SubPath subPath = SubPath.builder().build();
            if (myFindRoadSubPath.getTrafficType() == 1) {

                List<IssueSummary> issueDtoList = new ArrayList<>();
                List<Station> stations = new ArrayList<>();
                for (MyFindRoadStation myFindRoadStation : myFindRoadSubPath.getStations()) {
                    List<Issue> issueList = myFindRoadStation.getIssueList();
                    List<Issue> activeIssues = new ArrayList<>();
                    LocalDateTime currentDateTime = LocalDateTime.now();
                    if (issueList != null) {
                        for (Issue issue : issueList) {
                            if (issue.getExpireDate() != null && issue.getExpireDate().isAfter(currentDateTime)) {
                                activeIssues.add(issue);
                            }
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
                subPath = SubPath.builder()
                        .way(myFindRoadSubPath.getWay())
                        .door(myFindRoadSubPath.getDoor())
                        .trafficType(myFindRoadSubPath.getTrafficType())
                        .stationCount(myFindRoadSubPath.getStationCount())
                        .sectionTime(myFindRoadSubPath.getSectionTime())
                        .distance(myFindRoadSubPath.getDistance())
                        .name(myFindRoadSubPath.getName())
                        .stationCode(myFindRoadSubPath.getStationCode())
                        .direct(isDirect)
                        .issueSummary(IssueSummary.getIssueSummaryDtoByLine(issueDtoList))
                        .stations(stations)
                        .build();
            }
            subPaths.add(subPath);
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
    private static List<Issue> filterIssuesByUserSetting(
            List<Issue> issueList,
            boolean nextDayNotificationEnabled,
            LocalDateTime currentDateTime
    ) {
        LocalDateTime start = currentDateTime.withHour(0).withMinute(0).withSecond(1).withNano(0);
        LocalDateTime end   = currentDateTime.withHour(23).withMinute(59).withSecond(59).withNano(0);

        if (nextDayNotificationEnabled) {
            // 오후 9시 전/후 분기
            if (currentDateTime.getHour() < 21) {
                start = currentDateTime.minusDays(1).withHour(21).withMinute(0).withSecond(0).withNano(0);
                end   = currentDateTime.plusDays(1).withHour(23).withMinute(59).withSecond(59).withNano(0);
            } else {
                start = currentDateTime.withHour(21).withMinute(0).withSecond(0).withNano(0);
                end   = currentDateTime.plusDays(1).withHour(23).withMinute(59).withSecond(59).withNano(0);
            }
        }

        List<Issue> activeIssue = new ArrayList<>();
        if (issueList != null) {
            for (Issue issue : issueList) {
                if (issue.getStartDate() != null && issue.getExpireDate() != null && issue.getStartDate().isAfter(start) &&
                        issue.getExpireDate().isBefore(end)) {
                    activeIssue.add(issue);
                }
            }
        }
        return activeIssue;
    }

}