package com.gazi.gazi_renew.mock;

import com.gazi.gazi_renew.station.domain.Station;
import com.gazi.gazi_renew.station.service.port.SubwayRepository;
import jakarta.persistence.EntityNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class FakeSubwayRepository implements SubwayRepository {
    private final AtomicLong autoGeneratedId = new AtomicLong(0);
    private final List<Station> data = new ArrayList<>();
    @Override
    public List<Station> findByNameStartingWith(String name) {
        return data.stream()
                .filter(station -> station.getName().startsWith(name))
                .collect(Collectors.toList());
    }

    @Override
    public Station findCoordinateByNameAndLine(String name, String line) {
        return data.stream()
                .filter(station -> station.getName().equals(name))
                .filter(station -> station.getLine().equals(line))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Statin을 찾을 수 없습니다."));
    }
    @Override
    public List<Station> findByIssueStationCodeBetween(int lowerCode, int upperCode) {
        return data.stream()
                .filter(station -> station.getIssueStationCode() != null)
                .filter(station -> station.getIssueStationCode() >= lowerCode)
                .filter(station -> station.getIssueStationCode() <= upperCode)
                .collect(Collectors.toList());
    }

    @Override
    public List<Station> findByNameContainingAndLine(String name, String line) {
        return data.stream()
                .filter(station -> station.getName().contains(name))
                .filter(station -> station.getLine().equals(line))
                .collect(Collectors.toList());
    }
    @Override
    public void save(Station station) {
        if (station.getId() == null || station.getId() == 0) {
            Station createStation = Station.builder()
                    .id(autoGeneratedId.incrementAndGet())
                    .line(station.getLine())
                    .name(station.getName())
                    .stationCode(station.getStationCode())
                    .lat(station.getLat())
                    .lng(station.getLng())
                    .issueStationCode(station.getStationCode())
                    .build();

            data.add(createStation);
        }
        else{
            data.removeIf(item -> Objects.equals(item.getId(), station.getId()));
            data.add(station);
        }
    }
}
