package com.gazi.gazi_renew.station.service;

import com.gazi.gazi_renew.station.domain.Station;
import com.gazi.gazi_renew.station.service.port.SubwayRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StationService {

    private final SubwayRepository subwayRepository;
    public List<Station> getSubwayInfo(String subwayName) {
        List<Station> stationList = subwayRepository.findByNameStartingWith(subwayName);
        return stationList;
    }

    public List<Station> getNearByCoordinates(double latitude, double longitude) {
        List<Station> stationList = subwayRepository.getNearByCoordinates(latitude, longitude);
        return stationList;
    }

    public List<Station> getStationsByLine(String line) {
        List<Station> stationList = subwayRepository.findByLine(line);
        return stationList;
    }
}
