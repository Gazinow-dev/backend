package com.gazi.gazi_renew.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gazi.gazi_renew.domain.MyFindRoadPath;
import com.gazi.gazi_renew.domain.MyFindRoadSubPath;
import com.gazi.gazi_renew.domain.Station;
import com.gazi.gazi_renew.dto.MyFindRoadResponse;
import com.gazi.gazi_renew.dto.Response;
import com.gazi.gazi_renew.dto.SubwayDataResponse;
import com.gazi.gazi_renew.repository.SubwayRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public void saveStationsFromJsonFile() throws IOException, InterruptedException {
        ClassPathResource resource = new ClassPathResource("station_coordinate.json");
        ObjectMapper objectMapper = new ObjectMapper();
        Station[] stations = objectMapper.readValue(resource.getFile(), Station[].class);
        int i = 0;
        for (Station station : stations) {
            if(!subwayRepository.existsByStationCode(station.getStationCode())){
                subwayRepository.save(station);
            }
            i ++;
            if( i%200 == 0){
                Thread.sleep(3000);
            }
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
    public List<MyFindRoadResponse.transitStation> getTransitStation(MyFindRoadPath path) {

        List<MyFindRoadResponse.transitStation> transitStations = new ArrayList<>();
        String lastLine = "";

        MyFindRoadResponse.transitStation transitStation;
        if (path.getSubPaths().size() != 0) {
            for (int i = 0; i < path.getSubPaths().size(); i++) {
                MyFindRoadSubPath subPath = path.getSubPaths().get(i);
                // 지하철에서 인덱스가 0번인것만 추출하기
                if (subPath.getStations().size() != 0) {
                    transitStation = MyFindRoadResponse.transitStation.builder()
                            .stationName(subPath.getStations().get(0).getStationName()) // 지하철 역이름
                            .line(subPath.getLanes().get(0).getName()) // 호선 이름
                            .build();
                    transitStations.add(transitStation);
                    lastLine = subPath.getLanes().get(0).getName();
                }
            }
        }
        if (path.getLastEndStation() != "") {
            transitStation = MyFindRoadResponse.transitStation.builder()
                    .stationName(path.getLastEndStation())
                    .line(lastLine)
                    .build();
            transitStations.add(transitStation);
        }
        return transitStations;
    }

    @Transactional(readOnly = true)
    public Station getStationByNameAndLine(String name, String line){
        try{
            System.out.println(line);
            List<Station> stations = subwayRepository.findByNameContainingAndLine(name,line);

            Station stationResponse = stations.get(0);
            int k = 0;
            // 필터링
            if(!stations.isEmpty() && stations.size() >=2){

                for(Station station : stations){
                    int stationLength = station.getName().length(); // 찾은 entity 역글자수
                    int result = stationLength - name.length();

                    if(k > result){
                        stationResponse = station;
                        k = result;
                    }
                }
            }
            return stationResponse;
        }catch (EntityNotFoundException e){
            log.error(e.getMessage());
            return null;
        }


    }
}
