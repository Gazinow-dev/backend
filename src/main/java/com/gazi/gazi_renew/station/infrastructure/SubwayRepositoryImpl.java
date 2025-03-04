package com.gazi.gazi_renew.station.infrastructure;

import com.gazi.gazi_renew.station.domain.Station;
import com.gazi.gazi_renew.station.service.port.SubwayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class SubwayRepositoryImpl implements SubwayRepository {
    private final SubwayJpaRepository subwayJpaRepository;
    @Override
    public List<Station> findByNameStartingWith(String name) {
        return subwayJpaRepository.findByNameStartingWith(name).stream()
                .map(StationEntity::toModel).collect(Collectors.toList());
    }
    @Override
    public Station findCoordinateByNameAndLine(String name, String line) {
        return subwayJpaRepository.findByNameAndLine(name, line).toModel();
    }
    @Override
    public List<Station> findByIssueStationCodeBetween(int lowerCode, int upperCode) {
        return subwayJpaRepository.findByIssueStationCodeBetween(lowerCode, upperCode).stream()
                .map(StationEntity::toModel).collect(Collectors.toList());
    }

    @Override
    public List<Station> findByNameContainingAndLine(String name, String line) {
        return subwayJpaRepository.findByNameContainingAndLine(name, line).stream()
                .map(StationEntity::toModel).collect(Collectors.toList());
    }

    @Override
    public List<Station> findByLine(String line) {
        return subwayJpaRepository.findByLine(line).stream()
                .map(StationEntity::toModel).collect(Collectors.toList());
    }

    @Override
    public void save(Station station) {
        subwayJpaRepository.save(StationEntity.from(station));
    }

    @Override
    public Optional<Station> findById(Long id) {
        Optional<Station> station = subwayJpaRepository.findById(id).map(StationEntity::toModel);
        return station;
    }

    @Override
    public List<Station> getNearByCoordinates(double latitude, double longitude) {
        return subwayJpaRepository.findNearbyStations(latitude, longitude)
                .stream().map(StationEntity::toModel).collect(Collectors.toList());
    }

}
