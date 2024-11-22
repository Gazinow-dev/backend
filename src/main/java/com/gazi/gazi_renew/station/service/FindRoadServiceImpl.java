package com.gazi.gazi_renew.station.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gazi.gazi_renew.common.controller.port.SecurityUtilService;
import com.gazi.gazi_renew.common.controller.response.Response;
import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.domain.IssueStation;
import com.gazi.gazi_renew.issue.domain.dto.IssueSummary;
import com.gazi.gazi_renew.issue.service.port.IssueRepository;
import com.gazi.gazi_renew.issue.service.port.IssueStationRepository;
import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.service.port.MemberRepository;
import com.gazi.gazi_renew.route.domain.MyFindRoad;
import com.gazi.gazi_renew.route.service.port.MyFindRoadPathRepository;
import com.gazi.gazi_renew.station.controller.port.FindRoadService;
import com.gazi.gazi_renew.station.domain.Station;
import com.gazi.gazi_renew.station.domain.dto.FindRoadRequest;
import com.gazi.gazi_renew.station.controller.response.FindRoadResponse;
import com.gazi.gazi_renew.station.service.port.SubwayRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FindRoadServiceImpl implements FindRoadService {

    private final Response response;
    private final SubwayRepository subwayRepository;
    private final MemberRepository memberRepository;
    private final MyFindRoadPathRepository myFindRoadPathRepository;
    private final IssueStationRepository issueStationRepository;
    private final SecurityUtilService securityUtilService;
    @Value("${odsay.key}")
    public String apiKey;

    public JSONObject getJsonArray(String urlInfo) throws IOException {
        URL url = new URL(urlInfo.toString());
        System.out.println(url);
        System.out.println(apiKey);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader(conn.getInputStream()));

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }
        bufferedReader.close();
        conn.disconnect();

        String jsonData = sb.toString();
        JSONObject jsonObject = new JSONObject(jsonData);

        return jsonObject;
    }

    public JSONObject getJsonArray(Double sx, Double sy, Double ex, Double ey) throws IOException {

        System.out.println("apiKey: " + apiKey);
        StringBuilder urlInfo = new StringBuilder("https://api.odsay.com/v1/api"); /*URL*/
        urlInfo.append("/" + URLEncoder.encode("searchPubTransPathT", "UTF-8"));
        urlInfo.append("?LANG=" + URLEncoder.encode(String.valueOf(1), "UTF-8"));
        urlInfo.append("&SX=" + URLEncoder.encode(String.valueOf(sx), "UTF-8"));
        urlInfo.append("&SY=" + URLEncoder.encode(String.valueOf(sy), "UTF-8"));
        urlInfo.append("&EX=" + URLEncoder.encode(String.valueOf(ex), "UTF-8"));
        urlInfo.append("&EY=" + URLEncoder.encode(String.valueOf(ey), "UTF-8"));
        urlInfo.append("&OPT=" + URLEncoder.encode("0", "UTF-8"));
        urlInfo.append("&SearchPathType=" + URLEncoder.encode("1", "UTF-8"));
        urlInfo.append("&apiKey=" + URLEncoder.encode(apiKey, "UTF-8"));

        return getJsonArray(urlInfo.toString());
    }

    @Override
    public ResponseEntity<Response.Body> subwayRouteSearch(Long CID, Long SID, Long EID, int sopt) throws IOException {
        StringBuilder urlInfo = new StringBuilder("https://api.odsay.com/v1/api"); /*URL*/
        urlInfo.append("/" + URLEncoder.encode("subwayPath", "UTF-8"));
        urlInfo.append("?CID=" + URLEncoder.encode(String.valueOf(CID), "UTF-8"));
        urlInfo.append("&SID=" + URLEncoder.encode(String.valueOf(SID), "UTF-8"));
        urlInfo.append("&EID=" + URLEncoder.encode(String.valueOf(EID), "UTF-8"));
        urlInfo.append("&sopt=" + URLEncoder.encode(String.valueOf(sopt), "UTF-8"));
        urlInfo.append("&apiKey=" + URLEncoder.encode(apiKey, "UTF-8"));
        JSONObject json = getJsonArray(urlInfo.toString());
        return response.success(json, "길 찾기 데이터 전송 완료", HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<Response.Body> findRoad(FindRoadRequest findRoadRequest) throws IOException {

        Optional<Member> member = memberRepository.getReferenceByEmail(securityUtilService.getCurrentUserEmail());

        // 출발역 이름과 호선으로 데이터 찾기
        Station startStation = subwayRepository.findCoordinateByNameAndLine(findRoadRequest.getStrStationName(), findRoadRequest.getStrStationLine());
        // 종착역 이름과 호선으로 데이터 찾기
        Station endStation = subwayRepository.findCoordinateByNameAndLine(findRoadRequest.getEndStationName(), findRoadRequest.getEndStationLine());

        JSONObject json = getJsonArray(startStation.getLng(), startStation.getLat(), endStation.getLng(), endStation.getLat());

        String jsonString = json.toString();

        // Jackson ObjectMapper 생성
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // JSON 데이터를 JsonNode로 읽기
            JsonNode jsonNode = objectMapper.readTree(jsonString);

            // FindRoadResponse 객체로 변환
            FindRoadResponse findRoadResponse = new FindRoadResponse();
            findRoadResponse.setStationCount(jsonNode.path("result").path("subwayCount").asInt());

            // paths 배열 처리
            ArrayList<FindRoadResponse.Path> paths = new ArrayList<>();
            JsonNode pathArray = jsonNode.path("result").path("path");
            for (JsonNode pathNode : pathArray) {
                FindRoadResponse.Path path = new FindRoadResponse.Path();
                path.setTotalTime(pathNode.path("info").path("totalTime").asInt());
                path.setStationTransitCount(pathNode.path("info").path("stationTransitCount").asInt());
                path.setFirstStartStation(pathNode.path("info").path("firstStartStation").asText());
                path.setLastEndStation(pathNode.path("info").path("lastEndStation").asText());

                if (member.isPresent()) {
                    Optional<List<MyFindRoad>> myFindRoadPath = myFindRoadPathRepository.findAllByFirstStartStationAndLastEndStationAndMemberAndTotalTime(
                            pathNode.path("info").path("firstStartStation").asText(),
                            pathNode.path("info").path("lastEndStation").asText(),
                            member.get(),
                            pathNode.path("info").path("totalTime").asInt()
                    );
                    List<Long> myPathId = new ArrayList<>();

                    if (myFindRoadPath.isPresent() && !myFindRoadPath.get().isEmpty()) {
                        path.setMyPath(true);
                        for (MyFindRoad myFindRoad : myFindRoadPath.get()) {
                            myPathId.add(myFindRoad.getId());
                        }
                        path.setMyPathId(myPathId);
                    } else {
                        path.setMyPath(false);
                    }
                } else {
                    path.setMyPath(false);
                }

                // subPaths 배열 처리
                ArrayList<FindRoadResponse.SubPath> subPaths = new ArrayList<>();
                JsonNode subPathArray = pathNode.path("subPath");
                for (JsonNode subPathNode : subPathArray) {
                    if (subPathNode.path("trafficType").asInt() == 1 || subPathNode.path("trafficType").asInt() == 3) {
                        FindRoadResponse.SubPath subPath = new FindRoadResponse.SubPath();
                        subPath.setTrafficType(subPathNode.path("trafficType").asInt());
                        subPath.setDistance(subPathNode.path("distance").asDouble());
                        subPath.setSectionTime(subPathNode.path("sectionTime").asInt());
                        subPath.setStationCount(subPathNode.path("stationCount").asInt());
                        subPath.setDoor(subPathNode.path("door").asText());

                        // `lane` 데이터를 SubPath 필드에 직접 설정
                        JsonNode laneArray = subPathNode.path("lane");
                        if (laneArray.size() > 0) {
                            JsonNode firstLane = laneArray.get(0); // 첫 번째 lane 데이터를 가져옴
                            subPath.setName(firstLane.path("name").asText());
                            subPath.setStationCode(firstLane.path("subwayCode").asInt());
                            if(firstLane.path("name").asText().contains("(급행)")){
                                subPath.setDirect(true);
                            }
                            else {
                                subPath.setDirect(false);
                            }
                        }

                        // passStopList 처리
                        JsonNode passStopListNode = subPathNode.path("passStopList");
                        ArrayList<FindRoadResponse.Station> stations = new ArrayList<>();
                        JsonNode stationArray = passStopListNode.path("stations");

                        // 오디세이에서 설정한 `way` 값을 `다음역`으로 설정
                        if (stationArray.size() >= 2) {
                            subPath.setWay(stationArray.get(1).path("stationName").asText());
                        }

                        // 역별 이슈 처리
                        List<IssueSummary> issueDtoList = new ArrayList<>();
                        for (JsonNode stationNode : stationArray) {
                            FindRoadResponse.Station station = new FindRoadResponse.Station();
                            station.setIndex(stationNode.path("index").asInt());
                            station.setStationName(stationNode.path("stationName").asText());
                            station.setStationCode(stationNode.path("stationID").asInt());

                            // 역 이슈 조회 및 설정
                            String lineName = subPath.getName(); // 급행 포함한 이름 그대로 사용
                            if(lineName.equals("수도권 9호선(급행)")){
                                lineName = "수도권 9호선";
                            }
                            List<Station> stationList = subwayRepository.findByNameContainingAndLine(stationNode.path("stationName").asText(), lineName);
                            Station stationName = Station.toFirstStation(stationNode.path("stationName").asText(), stationList);
                            List<IssueStation> issueStationList = issueStationRepository.findAllByStationId(stationName.getId());
                            List<Issue> activeIssues = issueStationList.stream()
                                    .map(IssueStation::getIssue)
                                    .filter(issue -> {
                                        LocalDateTime now = LocalDateTime.now();
                                        return now.isAfter(issue.getStartDate()) && now.isBefore(issue.getExpireDate());
                                    })
                                    .collect(Collectors.toList());
                            List<IssueSummary> issueSummaryDto = IssueSummary.getIssueSummaryDto(activeIssues);
                            issueDtoList.addAll(issueSummaryDto);
                            station.setIssueSummary(issueSummaryDto);
                            stations.add(station);
                        }

                        // 호선 이슈 설정
                        subPath.setIssueSummary(IssueSummary.getIssueSummaryDtoByLine(issueDtoList));
                        subPath.setStations(stations);
                        subPaths.add(subPath);
                    }
                }

                if (!subPaths.isEmpty()) {
                    path.setSubPaths(subPaths);
                    path.setTransitStationList(getTransitStation(path));
                }

                paths.add(path);
            }
            findRoadResponse.setPaths(paths);

            return response.success(findRoadResponse, "길 찾기 데이터 전송 완료", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return response.fail("실패", HttpStatus.BAD_REQUEST);
        }
    }

    public ArrayList<FindRoadResponse.TransitStation> getTransitStation(FindRoadResponse.Path path) {

        ArrayList<FindRoadResponse.TransitStation> transitStations = new ArrayList<>();
        String lastLine = "";

        FindRoadResponse.TransitStation transitStation;
        if (path.getSubPaths().size() != 0) {
            for (int i = 0; i < path.getSubPaths().size(); i++) {
                FindRoadResponse.SubPath subPath = path.getSubPaths().get(i);
                if (subPath.getStations().size() != 0) {
                    transitStation = FindRoadResponse.TransitStation.builder()
                            .stationsName(subPath.getStations().get(0).getStationName()) // 지하철 역이름
                            .line(subPath.getName()) // 호선 이름
                            .build();
                    transitStations.add(transitStation);
                    lastLine = subPath.getName();
                }
            }
        }
        if (path.getLastEndStation() != "") {
            transitStation = FindRoadResponse.TransitStation.builder()
                    .stationsName(path.getLastEndStation())
                    .line(lastLine)
                    .build();
            transitStations.add(transitStation);
        }
        return transitStations;
    }
}
