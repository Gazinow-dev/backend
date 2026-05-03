package com.gazi.gazi_renew.station.service;

import com.gazi.gazi_renew.station.controller.response.StationResponse;
import com.gazi.gazi_renew.station.domain.Station;
import com.gazi.gazi_renew.station.service.port.SubwayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StationService {

    private static final String ALL_LINES = "전체";

    private final SubwayRepository subwayRepository;

    public List<Station> getSubwayInfo(String subwayName) {
        return subwayRepository.findByNameStartingWith(subwayName);
    }

    public List<Station> getNearByCoordinates(double latitude, double longitude) {
        return subwayRepository.getNearByCoordinates(latitude, longitude);
    }

    public List<StationResponse> getStationsByLine(String line) {
        if (ALL_LINES.equals(line)) {
            return getAllStationsSortedByName();
        }
        return getStationsByLineWithAllLines(line);
    }

    // 전체 역 조회 - 1 쿼리, ㄱㄴㄷ 정렬
    private List<StationResponse> getAllStationsSortedByName() {
        List<Station> allStations = subwayRepository.findAll();

        Map<String, List<Station>> groupedByName = allStations.stream()
                .collect(Collectors.groupingBy(Station::getName, LinkedHashMap::new, Collectors.toList()));

        return groupedByName.entrySet().stream()
                .map(entry -> {
                    Station representative = entry.getValue().get(0);
                    List<String> lines = entry.getValue().stream()
                            .map(Station::getLine)
                            .sorted()
                            .collect(Collectors.toList());
                    return StationResponse.from(representative, lines);
                })
                .sorted(Comparator.comparing(StationResponse::getName))
                .collect(Collectors.toList());
    }

    // 특정 노선 조회 - 2 쿼리, 환승역 노선 집계
    private List<StationResponse> getStationsByLineWithAllLines(String line) {
        List<Station> lineStations = subwayRepository.findByLine(line);

        List<String> stationNames = lineStations.stream()
                .map(Station::getName)
                .distinct()
                .collect(Collectors.toList());

        Map<String, List<String>> linesByName = subwayRepository.findByNameIn(stationNames).stream()
                .collect(Collectors.groupingBy(
                        Station::getName,
                        Collectors.mapping(Station::getLine, Collectors.toList())
                ));

        // lineStations 순서 유지하면서 역명 기준 중복 제거
        Map<String, Station> representativeByName = new LinkedHashMap<>();
        for (Station station : lineStations) {
            representativeByName.putIfAbsent(station.getName(), station);
        }

        return representativeByName.values().stream()
                .map(representative -> {
                    List<String> lines = linesByName.getOrDefault(representative.getName(), new ArrayList<>(List.of(line)));
                    lines.sort(Comparator.naturalOrder());
                    return StationResponse.from(representative, lines);
                })
                .collect(Collectors.toList());
    }
}
