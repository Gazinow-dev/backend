package com.gazi.gazi_renew.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gazi.gazi_renew.domain.MyFindRoadPath;
import com.gazi.gazi_renew.domain.MyFindRoadSubPath;
import com.gazi.gazi_renew.domain.Station;
import com.gazi.gazi_renew.dto.MyFindRoadResponse;
import com.gazi.gazi_renew.dto.Response;
import com.gazi.gazi_renew.dto.SubwayDataResponse;
import com.gazi.gazi_renew.repository.SubwayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubwayDataService {

    private final Response response;
    private final SubwayRepository subwayRepository;

    public void saveStationsFromJsonFile() throws IOException {
        ClassPathResource resource = new ClassPathResource("station_coordinate.json");
        ObjectMapper objectMapper = new ObjectMapper();
        Station[] stations = objectMapper.readValue(resource.getFile(), Station[].class);

        for (Station station : stations) {
            subwayRepository.save(station);
        }
        log.info("지하철 정보 업로드 완료");
    }

    public SubwayDataResponse getCoordinateByNameAndLine(String subwayName, String line) {
        SubwayDataResponse subwayCoordinates = subwayRepository.findCoordinateByNameAndLine(subwayName, line);
        return subwayCoordinates;
    }

    public ResponseEntity<Response.Body> getSubwayInfo(String subwayName) {
        List<Station> stations = subwayRepository.findByNameStartingWith(subwayName);
        List<SubwayDataResponse.SubwayInfo> subwayInfos = stations.stream()
                .map(
                        station -> {
                            SubwayDataResponse.SubwayInfo subwayInfo = SubwayDataResponse.SubwayInfo.builder()
                                    .name(station.getName())
                                    .line(station.getLine())
                                    .build();
                            return subwayInfo;
                        }
                ).collect(Collectors.toList());

        return response.success(subwayInfos, "지하철 검색 성공", HttpStatus.OK);
    }

    //환승역 추출 서비스
    public List<MyFindRoadResponse.Station> getTransitStation(MyFindRoadPath path) {

        List<MyFindRoadResponse.Station> Stations = new ArrayList<>();
        String lastLine = "";

        MyFindRoadResponse.Station Station;
        if (path.getSubPaths().size() != 0) {
            for (int i = 0; i < path.getSubPaths().size(); i++) {
                MyFindRoadSubPath subPath = path.getSubPaths().get(i);
                // 지하철에서 인덱스가 0번인것만 추출하기
                if (subPath.getStations().size() != 0) {
                    Station = MyFindRoadResponse.Station.builder()
                            .stationName(subPath.getStations().get(0).getStationName()) // 지하철 역이름
                            .line(subPath.getLanes().get(0).getName()) // 호선 이름
                            .build();
                    Stations.add(Station);
                    lastLine = subPath.getLanes().get(0).getName();
                }
            }
        }
        if (path.getLastEndStation() != "") {
            Station = MyFindRoadResponse.Station.builder()
                    .stationName(path.getLastEndStation())
                    .line(lastLine)
                    .build();
            Stations.add(Station);
        }
        return Stations;
    }
}
