package com.gazi.gazi_renew.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gazi.gazi_renew.dto.FindRoadRequest;
import com.gazi.gazi_renew.dto.FindRoadResponse;
import com.gazi.gazi_renew.dto.Response;
import com.gazi.gazi_renew.dto.SubwayDataResponse;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

@RequiredArgsConstructor
@Service
public class FindRoadServiceImpl implements FindRoadService {

    private final Response response;
    private final SubwayDataService subwayDataService;
    @Value("${odsay.key}")
    public static String apiKey;

    public JSONObject getJsonArray(String urlInfo) throws IOException {
        URL url = new URL(urlInfo.toString());
        System.out.println(url);
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

        StringBuilder urlInfo = new StringBuilder("https://api.odsay.com/v1/api"); /*URL*/
        urlInfo.append("/" + URLEncoder.encode("searchPubTransPathT", "UTF-8"));
        urlInfo.append("?LANG=" + URLEncoder.encode(String.valueOf(1), "UTF-8"));
        urlInfo.append("&SX=" + URLEncoder.encode(String.valueOf(sx), "UTF-8"));
        urlInfo.append("&SY=" + URLEncoder.encode(String.valueOf(sy), "UTF-8"));
        urlInfo.append("&EX=" + URLEncoder.encode(String.valueOf(ex), "UTF-8"));
        urlInfo.append("&EY=" + URLEncoder.encode(String.valueOf(ey), "UTF-8"));
        urlInfo.append("&OPT=" + URLEncoder.encode("0", "UTF-8"));
        urlInfo.append("&SearchPathType=" + URLEncoder.encode("1", "UTF-8"));
        urlInfo.append("&apiKey=" + URLEncoder.encode("mdn3gFOpu1TYxWEF80iAU4Fmlo2/OSQruUG1Vqw18Xw", "UTF-8"));

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
    public ResponseEntity<Response.Body> findRoad(FindRoadRequest request) throws IOException {

        // 출발역 이름과 호선으로 데이터 찾기
        SubwayDataResponse strSubwayInfo = subwayDataService.getCoordinateByNameAndLine(request.getStrSubwayName(), request.getStrSubwayLine());
        // 종착역 이름과 호선으로 데이터 찾기
        SubwayDataResponse endSubwayInfo = subwayDataService.getCoordinateByNameAndLine(request.getEndSubwayName(), request.getEndSubwayLine());

        JSONObject json = getJsonArray(strSubwayInfo.getLng(), strSubwayInfo.getLat(), endSubwayInfo.getLng(), endSubwayInfo.getLat());

        String jsonString = json.toString();

        // Jackson ObjectMapper 생성
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // JSON 데이터를 JsonNode로 읽기
            JsonNode jsonNode = objectMapper.readTree(jsonString);

            // FindRoadResponse 객체로 변환
            FindRoadResponse findRoadResponse = new FindRoadResponse();
            findRoadResponse.setSubwayCount(jsonNode.path("result").path("subwayCount").asInt());

            // paths 배열 처리
            ArrayList<FindRoadResponse.Path> paths = new ArrayList<>();
            JsonNode pathArray = jsonNode.path("result").path("path");
            for (JsonNode pathNode : pathArray) {
                FindRoadResponse.Path path = new FindRoadResponse.Path();
                path.setTotalTime(pathNode.path("info").path("totalTime").asInt());
                path.setSubwayTransitCount(pathNode.path("info").path("subwayTransitCount").asInt());
                path.setFirstStartStation(pathNode.path("info").path("firstStartStation").asText());
                path.setLastEndStation(pathNode.path("info").path("lastEndStation").asText());

                // subPaths 배열 처리
                ArrayList<FindRoadResponse.SubPath> subPaths = new ArrayList<>();
                JsonNode subPathArray = pathNode.path("subPath");
                for (JsonNode subPathNode : subPathArray) {
                    if (subPathNode.path("trafficType").asInt() == 1) {
                        FindRoadResponse.SubPath subPath = new FindRoadResponse.SubPath();
                        subPath.setTrafficType(subPathNode.path("trafficType").asInt());
                        subPath.setDistance(subPathNode.path("distance").asDouble());
                        subPath.setSectionTime(subPathNode.path("sectionTime").asInt());
                        subPath.setStationCount(subPathNode.path("stationCount").asInt());

                        // lanes 배열 처리
                        ArrayList<FindRoadResponse.Lane> lanes = new ArrayList<>();
                        JsonNode laneArray = subPathNode.path("lane");
                        for (JsonNode laneNode : laneArray) {
                            FindRoadResponse.Lane lane = new FindRoadResponse.Lane();
                            lane.setName(laneNode.path("name").asText());
                            lane.setSubwayCode(laneNode.path("subwayCode").asInt());
                            lane.setStartName(laneNode.path("startName").asText());
                            lane.setEndName(laneNode.path("endName").asText());

                            lanes.add(lane);
                        }
                        subPath.setLanes(lanes);

                        // passStopList 처리
                        JsonNode passStopListNode = subPathNode.path("passStopList");
                        ArrayList<FindRoadResponse.Subway> subways = new ArrayList<>();
                        JsonNode stationArray = passStopListNode.path("stations");
                        for (JsonNode stationNode : stationArray) {
                            FindRoadResponse.Subway station = new FindRoadResponse.Subway();
                            station.setIndex(stationNode.path("index").asInt());
                            station.setStationName(stationNode.path("stationName").asText());
                            subways.add(station);
                        }
                        subPath.setSubways(subways);
                        subPaths.add(subPath);
                    }

                }
                if (subPaths.size() != 0) {
                    path.setSubPaths(subPaths);
                    path.setTransitStations(getTransitStation(path));
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
                if (subPath.getSubways().size() != 0) {
                    transitStation = FindRoadResponse.TransitStation.builder()
                            .subwayName(subPath.getSubways().get(0).getStationName()) // 지하철 역이름
                            .line(subPath.getLanes().get(0).getName()) // 호선 이름
                            .build();
                    transitStations.add(transitStation);
                    lastLine = subPath.getLanes().get(0).getName();
                }
            }
        }
        if (path.getLastEndStation() != "") {
            transitStation = FindRoadResponse.TransitStation.builder()
                    .subwayName(path.getLastEndStation())
                    .line(lastLine)
                    .build();
            transitStations.add(transitStation);
        }
        return transitStations;
    }
}
